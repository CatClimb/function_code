package com.weipu.dx45gdata.collectdata1.task;




import com.weipu.dx45gdata.common.entity.AbData;
import com.weipu.dx45gdata.common.entity.FtpConfig;
import com.weipu.dx45gdata.common.entity.FtpConfig2;
import com.weipu.dx45gdata.common.service.AbDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPInputStream;

@Slf4j

@Service
public class DownLoadTask {

    @Resource
    private FtpConfig ftpConfig;

    @Resource
    private FtpConfig2 ftpConfig2;

    @Resource
    private AbDataService abDataService;


    //连接ftp
     public FTPClient ftpConnection(String host, int port, String username, String password) throws IOException {

            FTPClient ftpClient = new FTPClient( );
//        try {
                ftpClient.setControlEncoding("GBK");
                //连接ftp
                ftpClient.connect(host, port);
                ftpClient.login(username, password);
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode( );
                //是否成功登录服务器
                int replyCode = ftpClient.getReplyCode( );
                if (!FTPReply.isPositiveCompletion(replyCode)) {
                    ftpClient.disconnect( );
                    log.info("连接失败");
                    System.exit(1);
                }
//        }catch (Exception e){
//            log.info("2");
//        }
        return ftpClient;
    }

    //接入工参
    @Scheduled(cron = "${ftp.cron}")
    public void downLoadCsv()  {

            try {
            long start_time = System.currentTimeMillis( );
            MDC.put("start_time", String.valueOf(start_time));
            MDC.put("sql_count",String.valueOf(0));
            String path = ftpConfig.getSavePath( );
            //文件存放路径


            //连接ftp
            MDC.put("px_name", " downLoadCsv() ftpConnection()");
            FTPClient ftpClient = this.ftpConnection(ftpConfig.getHost( ), ftpConfig.getPort( ), ftpConfig.getUsername( ), ftpConfig.getPassword( ));
//      ftp下载
            MDC.put("px_name", " downLoadCsv() deleteDir()");
            deleteDir(ftpConfig.getSavePath( ));
            MDC.put("px_name", " downLoadCsv() downLoadFiles()");
            this.downLoadFiles2(ftpClient, path, ftpConfig.getDir( ));
            MDC.put("end_time", String.valueOf(System.currentTimeMillis()));
            MDC.put("px_name", " downLoadCsv()");
            log.info("采集成功");
            }catch (Exception e){
                log.error(e.toString());
                e.printStackTrace();
            }
    }
    //本地-ftp下载文件
    public boolean downLoadFiles(FTPClient ftpClient, String path, String dir) throws IOException {
        boolean flag = true;
        FileOutputStream os = null;
        int count=0;
        int size=0;
        FTPFile[] fs;


        ftpClient.changeWorkingDirectory(dir);
        fs = ftpClient.listFiles( );
        if (fs != null) {
            //进行遍历
            for (FTPFile ff : fs) {
                //文件名
                String name = ff.getName( );
                //存放路径+文件名
                String name2 = path +"/"+ name;
                System.out.printf(name2);

                //文件夹目录不存在创建目录
                File localFile = new File(name2);
                if (!localFile.getParentFile( ).exists( )) {
                    localFile.getParentFile( ).mkdirs( );
                    localFile.createNewFile( );

                }
                os = new FileOutputStream(name2);
                //下载
                MDC.put("px_name", " downLoadCsv() downLoadFiles() retrieveFile()");
                flag = ftpClient.retrieveFile(name, os);
//                log.info("下载"+(flag?"成功":"失败"));
                MDC.put("px_name", " downLoadCsv() downLoadFiles() readCsv()");
                List<AbData> list =readCsv(name2);
                size = list.size( );
//                log.info("采集的记录数：" + list.size( ));

                if (size > 0) {
                    MDC.put("px_name", " abDataService.addAbData()");
                     count = abDataService.addAbDataList(list);
                }
                MDC.put("sql_count", String.valueOf(count));
                if (os != null) {
                    try {
                        os.close( );
                    } catch (IOException e) {
                        e.printStackTrace( );
                    }
                }
            }
        }

        return flag;
    }

    public boolean downLoadFiles2(FTPClient ftpClient, String path, String dir) throws IOException {
        boolean flag = true;
        FileOutputStream os =null;
        int size=0;
        int count=0;

        //前一天的日期
        long nowTime = System.currentTimeMillis();
        long todayStartTime = nowTime - ((nowTime + TimeZone.getDefault().getRawOffset()) % (24 * 60 * 60 * 1000L)+5*(24 * 60 * 60 * 1000L)+(24 * 60 * 60 * 1000L));
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date(todayStartTime);
        String format = df.format(date);
        String dir2=dir+"/"+format;
        System.out.println("文件夹目录: "+dir2);
        ftpClient.changeWorkingDirectory(dir2); //进入到远程文件夹

        String value="tab_cel_extend-colum-";
        String value2=".csv.gz";
        String value3=".csv";
        String Downloadpath=value+format+value2;
        String Downloadname=value+format+value3;
        System.out.println("压缩包文件名"+Downloadpath);

        //文件夹目录不存在创建目录
        File localFile = new File(path+"/"+Downloadpath);
        if (!localFile.getParentFile().exists()) {
            localFile.getParentFile().mkdirs();
            localFile.createNewFile();
        }
        os = new FileOutputStream(path+"/"+Downloadpath);
        //下载
        MDC.put("px_name", " downLoadCsv() downLoadFiles2() retrieveFile()");
        ftpClient.retrieveFile(Downloadpath, os);
        if(os!=null){
            try{
                os.close();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        System.out.println("下载成功！");
        MDC.put("px_name", " downLoadCsv() downLoadFiles2() doUncompressFile()");
        doUncompressFile(path+"/"+Downloadpath);
        System.out.println("解压成功!");
        MDC.put("px_name", " downLoadCsv() downLoadFiles2() readCsv()");
        List<AbData> list = this.readCsv(path+"/"+Downloadname);
        size = list.size( );
        System.out.println("总数"+size);
        if (size > 0) {
            MDC.put("px_name", " downLoadCsv() downLoadFiles2() abDataService.addAbData()");
            count = abDataService.addAbDataList(list);
        }
        MDC.put("sql_count", String.valueOf(count));

        return flag;

    }

    public boolean downLoadFiles3(FTPClient ftpClient, String path, String dir,String date) throws IOException {
        boolean flag = true;
        FileOutputStream os =null;
        int size=0;
        int count=0;
        String format = date;
        String dir2=dir+"/"+format;
        System.out.println("文件夹目录: "+dir2);
        ftpClient.changeWorkingDirectory(dir2); //进入到远程文件夹

        String value="tab_cel_extend-colum-";
        String value2=".csv.gz";
        String value3=".csv";
        String Downloadpath=value+format+value2;
        String Downloadname=value+format+value3;
        System.out.println("压缩包文件名"+Downloadpath);

        //文件夹目录不存在创建目录
        File localFile = new File(path+"/"+Downloadpath);
        if (!localFile.getParentFile().exists()) {
            localFile.getParentFile().mkdirs();
            localFile.createNewFile();
        }
        os = new FileOutputStream(path+"/"+Downloadpath);
        //下载
        MDC.put("px_name", " supplementaryData() downLoadFiles3() retrieveFile()");
        ftpClient.retrieveFile(Downloadpath, os);
        System.out.println("下载成功！");
        if(os!=null){
            try{
                os.close();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        MDC.put("px_name", " downLoadCsv() downLoadFiles3() doUncompressFile()");
        doUncompressFile(path+"/"+Downloadpath);
        System.out.println("解压成功!");
        MDC.put("px_name", " downLoadCsv() downLoadFiles3() readCsv()");
        List<AbData> list = this.readCsv(path+"/"+Downloadname);
        size = list.size( );
        System.out.println("总数"+size);
        if (size > 0) {
            MDC.put("px_name", " supplementaryData() downLoadFiles3() abDataService.addAbData()");
            count = abDataService.addAbDataList(list);
        }
        MDC.put("sql_count", String.valueOf(count));

        return flag;
    }
    private void doUncompressFile(String inFileName) throws IOException {
        GZIPInputStream in = null;
        FileOutputStream out = null;
        try {

            if (!"gz".equalsIgnoreCase(getExtension(inFileName))) {
                log.error("File name must have extension of \".gz\"");
                System.exit(1);
            }

            System.out.println("Opening the compressed file.");

            try {
                in = new GZIPInputStream(new FileInputStream(inFileName));
            } catch (FileNotFoundException e) {
               log.error("File not found. " + inFileName);

                System.exit(1);
            }

            System.out.println("Open the output file.");
            String outFileName = getFileName(inFileName);

            try {
                out = new FileOutputStream(outFileName);
            } catch (FileNotFoundException e) {
                log.error("Could not write to file. " + outFileName);
                System.exit(1);
            }
            System.out.println("Transfering bytes from compressed file to the output file.");
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            System.out.println("Closing the file and stream");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }finally {
            if (in!=null){
                in.close();
            }
            if(out!=null){
                out.close();
            }
        }

    }
    public String getFileName(String f) {
         System.out.println( "xx:  "+f );
        String fname = "";
        int i = f.lastIndexOf('.');

        if (i > 0 && i < f.length() - 1) {
            fname = f.substring(0, i);
        }
        return fname;
    }
    public String getExtension(String f) {
        String ext = "";
        int i = f.lastIndexOf('.');

        if (i > 0 && i < f.length() - 1) {
            ext = f.substring(i + 1);
        }
        return ext;
    }
    //根据|分割CSV
    public static List<AbData> readCsv(String path) throws IOException {
        List<AbData> ablist = new ArrayList<AbData>( );
        int province_id_length = 0;
        int province_name_length = 0;
        int city_id_length = 0;
        int city_name_length = 0;
        int district_id_length = 0;
        int district_name_length = 0;
        int gnbid_length = 0;  //5G gnbic
        int enb_id_length = 0;
        int cell_no_length = 0;
        int enb_cellno_length = 0;
        int gnb_cell_id_length = 0;  //5G gnb_cell_id
        int lng_length = 0;
        int lat_length = 0;
        int address_length = 0;
        int azimuth_length = 0;
        int rate_length = 0;
        int is_indoor_length = 0;
        int sitecode_length = 0;
        int type_length = 0;
        DataInputStream in = null;
        BufferedReader br = null;

        in = new DataInputStream(new FileInputStream(new File(path)));
        //设置编码类型
//            BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"),3*1024*1024);
        br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line = "";
        String everyLine = "";
        int lineNum = 0;
        while ((line = br.readLine( )) != null) {
            everyLine = line;
            AbData abData = new AbData( );
            //将str转为为list
            List<String> strings = null;
            String[] split = everyLine.split("\\|", -1);
            //防止空行进行分隔
            if(!(split.length>=6)){
                continue;
            }
            List<String> strings1 = Arrays.asList(split);
            for (String s :
                    strings1) {
                System.out.print(s+" ");
            }
            System.out.println( );
            abData.setProvince_id(split[0]);
            abData.setProvince_name(split[1]);
            abData.setCity_id(split[2]);
            abData.setCity_name(split[3]);
            abData.setDistrict_id(split[4]);
            abData.setDistrict_name(split[5]);
//                if(lineNum==0){
//                    strings=Arrays.asList(everyLine.split("\\|", -1));
//                    System.out.println(strings);
//                    lineNum=lineNum+1;
//                }else {
//                    strings=Arrays.asList(everyLine.split("\\|", -1));
//                    String type = strings.get(type_length);
//                    String address = strings.get(address_length);
//                }
            ablist.add(abData);
        }

        if (in != null) {
            in.close( );
        }
        if (br != null) {
            br.close( );
        }
        return ablist;

    }
    //清除文件
    public static void deleteDir(String path) throws Exception {
        File file = new File(path);
        if (!file.exists( )) {
            throw new Exception( file.getPath()+": The dir are not exists!");
//            System.err.println("The dir are not exists!");
//            return false;
        }
        String[] content = file.list( );
        for (String name : content) {
            File temp = new File(path, name);
            if (temp.isDirectory( )) {
                deleteDir(temp.getAbsolutePath( ));
            } else {
                if (!temp.delete( )) {
//                    System.err.println("Failed to delete " + name);
                    throw new Exception( "Failed to delete " + name);
                }
            }
        }
//        return true;
    }

}
