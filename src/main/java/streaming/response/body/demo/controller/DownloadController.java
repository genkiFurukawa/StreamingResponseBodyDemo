package streaming.response.body.demo.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("download")
public class DownloadController {

    /**
     * zipファイルを生成しながら出力するサンプル
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<StreamingResponseBody> download() {

        // zipを作成しながらresponseを返す
        // 自分用メモ
        // 関数型インターフェースで一つしかメソッドないものははラムダ式で実装できる
        StreamingResponseBody responseBody = outputStream -> {

            File f = new File("/Users/genki/Desktop/test.jpg");

            try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {

                for (int i = 0; i < 2; i++) {
                    try (InputStream inputStream = new FileInputStream(f)) {
                        // パスを指定すると、zipの中にファイルが含まれる
                        ZipEntry zipEntry = new ZipEntry(i + "/" + f.getName());
                        zipOutputStream.putNextEntry(zipEntry);

                        byte[] bytes = new byte[1024];
                        int length;
                        while ((length = inputStream.read(bytes)) >= 0) {
                            zipOutputStream.write(bytes, 0, length);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        // レスポンスヘッダを作成
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/zip");
        // zipファイルの名前を指定できる
        responseHeaders.set("Content-Disposition", "attachment; filename=test.zip");

        ResponseEntity<StreamingResponseBody> response = new ResponseEntity<>(
                responseBody,
                responseHeaders,
                HttpStatus.OK);

        return response;
    }
}
