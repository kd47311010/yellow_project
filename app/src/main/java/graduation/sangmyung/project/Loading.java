package graduation.sangmyung.project;

/**
 * Created by hyunik on 2017-08-31.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import android.content.Context;
import android.content.Intent;

public class Loading implements Runnable {
    private MainLoading mContext;

    public Loading(MainLoading seoulTourActivity) {
        // TODO Auto-generated constructor stub
        mContext = seoulTourActivity;
    }

    public void run() {
        try {
            // 서버에 접속하여 notice.txt 파일(최신의 .db 파일 이름이 무엇인지 저장되어있음)을 다운로드
            // 최신의 .db 파일을 다운로드 후 MainMenuActivity로 전환됨
            // 2초동안 접속이 안되면 MainMenuActivity로 전환됨

            Socket socket = new Socket();
            SocketAddress addr = new InetSocketAddress("192.168.123.1", 5555);
            socket.connect(addr, 2000); // 2초간의 타임 아웃 여유, 2초 동안 접속이 안되면
            // SocketTimeoutException 발생시킴

            // socket = new Socket("192.168.123.1", 5555);
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            String name = "notice.txt";
            outputStream.write(name.getBytes());
            outputStream.flush();
            int readCount = 0;
            byte[] buffer = new byte[4096];

            FileOutputStream fileOutputStream = mContext.openFileOutput(name,
                    Context.MODE_WORLD_READABLE);

            readCount = inputStream.read(buffer);
            fileOutputStream.write(buffer, 0, readCount);
            fileOutputStream.flush();
            fileOutputStream.close();

            FileReader fr = new FileReader(
                    "/data/data/seoultour.project.team/files/notice.txt");
            BufferedReader br = new BufferedReader(fr);

            String str = br.readLine();
            br.close();
            outputStream.write(str.getBytes());
            outputStream.flush();

            int read = 0;
            byte[] buf = new byte[4096];

            FileOutputStream fileOut = mContext.openFileOutput(str,
                    Context.MODE_WORLD_READABLE);

            while ((read = inputStream.read(buf)) != -1) {

                fileOut.write(buf, 0, read);
            }

            fileOut.close();
            outputStream.close();
            inputStream.close();
            socket.close();

            FileInputStream fis = new FileInputStream(
                    "/data/data/seoultour.project.team/files/" + str);
            File file = new File("/data/data/seoultour.project.team/databases/");
            file.mkdir();
            FileOutputStream fos = new FileOutputStream(
                    "/data/data/seoultour.project.team/databases/" + str);
            byte[] buff = new byte[1024];
            int readC = 0;
            while (true) {
                readC = fis.read(buff, 0, 1024);

                if (readC == -1) {
                    break;
                }

                if (readC < 1024) {
                    fos.write(buff, 0, readC);
                    break;
                }

                fos.write(buff, 0, readC);
            }
            fos.flush();
            fos.close();
            fis.close();

            Intent intent = new Intent(mContext, MainActivity.class);
            intent.putExtra("timeout", false);
            mContext.startActivity(intent);
            mContext.finish();

        } catch (SocketTimeoutException e) {

            // 2초동안 접속이 안되면 timeout 되었다고 putExtra에 넣고 MainMenuActivity로 전환됨
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.putExtra("timeout", true);
            mContext.startActivity(intent);
            mContext.finish();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public MainLoading getmContext() {
        return mContext;
    }

    public void setmContext(MainLoading context) {
        this.mContext = context;
    }
}