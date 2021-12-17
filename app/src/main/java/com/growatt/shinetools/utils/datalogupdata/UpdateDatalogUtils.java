package com.growatt.shinetools.utils.datalogupdata;

import android.app.Activity;

import com.growatt.shinetools.modbusbox.CRC16;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.CRC32;

/**
 * 升级逻辑:
 * <p>
 * 1、APP启动时：从云端获取最新采集器升级文件（？考虑重复下载问题）：wifi-x.bin(1.5.0.0)、wifi-s.bin(1.6.0.0)
 * 2、APP配网时：与采集器通信：获取到采集器类型和版本号，判断此类型采集器有新版本，如果存在新版本，进行如下操作
 * 3、将采集器的固件切分成若干笔分包，存在集合中，然后通过TCP通讯一笔一笔下发。采集器固件分包规则如下：
 * 3.1、
 *
 * @author jehone gao
 * <p>
 * 2016-9-13
 */
public class UpdateDatalogUtils {

    // 需要下发的文件缓存集合
    public static List<ByteBuffer> UPDATE_FILE = new ArrayList<ByteBuffer>();
    // 采集器下发文件(升级)进度
    public static String DOWN_FILE_PROCESS = "0%";
    // 将文件分包，每笔1024KB
    public static final int INPUT_LENGTH_1024 = 1024;
    //将文件分包，每笔256B
    public static final int INPUT_LENGTH_256 = 256;

    public static void main(String[] args) throws IOException {
        System.out
                .println("1、APP启动时：从云端获取最新采集器升级文件（// TODO ，重复下载问题）：wifi-x.bin(1.5.0.0)、wifi-s.bin(1.6.0.0)");
        System.out
                .println("2、APP配网时：与采集器通信，获取到采集器类型和版本号，判断此类型采集器有新版本，如果存在新版本，进行如下操作");
        System.out.println("3、开始升级采集器准备工作：");
        UPDATE_FILE = getFile("升级文件路径：C:/1_5_0_0.BIN");
    }

    /**
     * 获取文件的分包，将文件按1024字节分成若干包
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static List<ByteBuffer> getFile(String filePath) throws IOException {
        UPDATE_FILE.clear();
        // 从手机本地获取文件
        File file = new File(filePath);
        // 1.定义变量
        List<ByteBuffer> byteBufferList = new ArrayList<ByteBuffer>();
        FileInputStream fis = null;
        FileChannel channel = null;
        MappedByteBuffer mbb = null;
        try {
            fis = new FileInputStream(file);
            channel = fis.getChannel();
            // 2.读取文件到ByteBuffer
            mbb = channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            System.out.println(".....................文件总长度:" + file.length());
            // 3.将文件转换成byte[]形式
            byte fileByte[] = new byte[(int) file.length()];
            int ind = 0;
            while (mbb.hasRemaining()) {
                fileByte[ind++] = mbb.get(); // 读取数据
            }
            // 4.获取整个文件的CRC32（后期检验用）
            byte[] crc32 = crc32(fileByte);
            // 5.整合文件及CRC检验码,生成新的byte数组
            byte[] newByteTemp = converData(crc32, fileByte);
            byte[] newByte = converData(newByteTemp, crc32);
            // 6.获取文件分包次数
            int count = ((newByte.length % INPUT_LENGTH_1024) == 0) ? (newByte.length / INPUT_LENGTH_1024)
                    : (newByte.length / INPUT_LENGTH_1024 + 1);
            System.out.println("..........................分包次数:" + count);
            // 7.将分包后数据转为ByteBuffer添加到List中
            for (int i = 0; i < count; i++) {
                // 定义分包
                ByteBuffer branchBuf = null;
                if (i == count - 1) {
                    int len = newByte.length - (INPUT_LENGTH_1024 * i);
                    branchBuf = ByteBuffer.allocate(len);
                    branchBuf.put(newByte, INPUT_LENGTH_1024 * i, len);
                } else {
                    branchBuf = ByteBuffer.allocate(INPUT_LENGTH_1024);
                    branchBuf.put(newByte, INPUT_LENGTH_1024 * i,
                            INPUT_LENGTH_1024);
                }
                branchBuf.flip();
                // 8.获取分包的crc16并整合分包
                byte[] branchByte = branchBuf.array();
                byte[] branchByteCRC16 = crc16(branchByte);
                byte[] resultByte = converData(branchByte, branchByteCRC16);
                byteBufferList.add(ByteBuffer.wrap(resultByte));
                System.out.println("当前是第" + i + "次添加");
            }
        } finally {
            try {
                channel.close();
                fis.close();
            } catch (Exception ex) {
                System.out.println("*********************生成文件分包时出错："
                        + ex.getMessage());
            }
        }
        return byteBufferList;
    }


    /**
     * 获取文件的分包，将文件按1024字节分成若干包
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static List<ByteBuffer> getFileByte(String filePath) throws IOException {
        UPDATE_FILE.clear();
        // 从手机本地获取文件
        File file = new File(filePath);
        // 1.定义变量
        List<ByteBuffer> byteBufferList = new ArrayList<ByteBuffer>();
        FileInputStream fis = null;
        FileChannel channel = null;
        MappedByteBuffer mbb = null;
        try {
            fis = new FileInputStream(file);
            channel = fis.getChannel();
            // 2.读取文件到ByteBuffer
            mbb = channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            System.out.println(".....................文件总长度:" + file.length());
            // 3.将文件转换成byte[]形式
            byte fileByte[] = new byte[(int) file.length()];
            int ind = 0;
            while (mbb.hasRemaining()) {
                fileByte[ind++] = mbb.get(); // 读取数据
            }
            // 4.获取整个文件的CRC32（后期检验用）放在第一包
            byte[] crc32 = crc32(fileByte);
            ByteBuffer branchBuf1 = ByteBuffer.allocate(crc32.length);
            branchBuf1.put(crc32, 0, crc32.length);
            branchBuf1.flip();
            byteBufferList.add(ByteBuffer.wrap(branchBuf1.array()));

            // 5.整合文件及CRC检验码,生成新的byte数组
      /*      byte[] newByteTemp = converData(crc32, fileByte);
            byte[] newByte = converData(newByteTemp, crc32);*/
            // 6.获取文件分包次数
            int count = ((fileByte.length % INPUT_LENGTH_1024) == 0) ? (fileByte.length / INPUT_LENGTH_1024)
                    : (fileByte.length / INPUT_LENGTH_1024 + 1);
            System.out.println("..........................分包次数:" + count);
            // 7.将分包后数据转为ByteBuffer添加到List中
            for (int i = 0; i < count; i++) {
                // 定义分包
                ByteBuffer branchBuf = null;
                if (i == count - 1) {
                    int len = fileByte.length - (INPUT_LENGTH_1024 * i);
                    branchBuf = ByteBuffer.allocate(len);
                    branchBuf.put(fileByte, INPUT_LENGTH_1024 * i, len);
                } else {
                    branchBuf = ByteBuffer.allocate(INPUT_LENGTH_1024);
                    branchBuf.put(fileByte, INPUT_LENGTH_1024 * i,
                            INPUT_LENGTH_1024);
                }
                branchBuf.flip();
                // 8.获取分包的crc16并整合分包
         /*       byte[] branchByte = branchBuf.array();
                byte[] branchByteCRC16 = crc16(branchByte);
                byte[] resultByte = converData(branchByte, branchByteCRC16);
                byteBufferList.add(ByteBuffer.wrap(resultByte));*/
                byteBufferList.add(branchBuf);
                System.out.println("当前是第" + i + "次添加");
            }
        } finally {
            try {
                channel.close();
                fis.close();
            } catch (Exception ex) {
                System.out.println("*********************生成文件分包时出错："
                        + ex.getMessage());
            }
        }
        return byteBufferList;
    }



    /**
     * 获取文件的分包，将文件按1024字节分成若干包
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static List<ByteBuffer> getFileByte2(Activity activity, String filePath) throws IOException {
        UPDATE_FILE.clear();
        // 从手机本地获取文件
        // 1.定义变量
        List<ByteBuffer> byteBufferList = new ArrayList<ByteBuffer>();
        try {
            InputStream inputStream = activity.getAssets().open(filePath);
            byte[] fileByte = readStreamToByte(inputStream);
            //2.第一个包20个字节
            byte[] crc32 = crc32(fileByte);
            LogUtil.i("文件CRC校验:"+ CommenUtils.bytesToHexString(crc32));
            ByteBuffer branchBuf1 = ByteBuffer.allocate(crc32.length);
            branchBuf1.put(crc32, 0, crc32.length);
            branchBuf1.flip();
            byteBufferList.add(ByteBuffer.wrap(branchBuf1.array()));

            // 5.整合文件及CRC检验码,生成新的byte数组
      /*      byte[] newByteTemp = converData(crc32, fileByte);
            byte[] newByte = converData(newByteTemp, crc32);*/
            // 6.获取文件分包次数
            int count = ((fileByte.length % INPUT_LENGTH_256) == 0) ? (fileByte.length / INPUT_LENGTH_256)
                    : (fileByte.length / INPUT_LENGTH_256 + 1);
            System.out.println("..........................分包次数:" + count);
            // 7.将分包后数据转为ByteBuffer添加到List中
            for (int i = 0; i < count; i++) {
                // 定义分包
                ByteBuffer branchBuf = null;
                if (i == count - 1) {
                    int len = fileByte.length - (INPUT_LENGTH_256 * i);
                    branchBuf = ByteBuffer.allocate(len);
                    branchBuf.put(fileByte, INPUT_LENGTH_256 * i, len);
                } else {
                    branchBuf = ByteBuffer.allocate(INPUT_LENGTH_256);
                    branchBuf.put(fileByte, INPUT_LENGTH_256 * i,
                            INPUT_LENGTH_256);
                }
                branchBuf.flip();
                // 8.获取分包的crc16并整合分包
         /*       byte[] branchByte = branchBuf.array();
                byte[] branchByteCRC16 = crc16(branchByte);
                byte[] resultByte = converData(branchByte, branchByteCRC16);
                byteBufferList.add(ByteBuffer.wrap(resultByte));*/
                byteBufferList.add(branchBuf);
                System.out.println("当前是第" + i + "次添加");
            }
        }catch (Exception ex) {
            System.out.println("*********************生成文件分包时出错："
                    + ex.getMessage());
        }
        return byteBufferList;
    }





    /**
     * 获取文件的分包，将文件按1024字节分成若干包
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static List<ByteBuffer> getFileByte256(String filePath) throws IOException {
        UPDATE_FILE.clear();
        // 从手机本地获取文件
        File file = new File(filePath);
        // 1.定义变量
        List<ByteBuffer> byteBufferList = new ArrayList<ByteBuffer>();
        FileInputStream fis = null;
        FileChannel channel = null;
        MappedByteBuffer mbb = null;
        try {
            fis = new FileInputStream(file);
            channel = fis.getChannel();
            // 2.读取文件到ByteBuffer
            mbb = channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            System.out.println(".....................文件总长度:" + file.length());
            // 3.将文件转换成byte[]形式
            byte fileByte[] = new byte[(int) file.length()];
            int ind = 0;
            while (mbb.hasRemaining()) {
                fileByte[ind++] = mbb.get(); // 读取数据
            }
            //2.第一个包20个字节
            byte[] crc32 = crc32(fileByte);
            LogUtil.i("文件CRC校验:"+ CommenUtils.bytesToHexString(crc32));
            ByteBuffer branchBuf1 = ByteBuffer.allocate(crc32.length);
            branchBuf1.put(crc32, 0, crc32.length);
            branchBuf1.flip();
            byteBufferList.add(ByteBuffer.wrap(branchBuf1.array()));

            // 5.整合文件及CRC检验码,生成新的byte数组
      /*      byte[] newByteTemp = converData(crc32, fileByte);
            byte[] newByte = converData(newByteTemp, crc32);*/
            // 6.获取文件分包次数
            int count = ((fileByte.length % INPUT_LENGTH_256) == 0) ? (fileByte.length / INPUT_LENGTH_256)
                    : (fileByte.length / INPUT_LENGTH_256 + 1);
            System.out.println("..........................分包次数:" + count);
            // 7.将分包后数据转为ByteBuffer添加到List中
            for (int i = 0; i < count; i++) {
                // 定义分包
                ByteBuffer branchBuf = null;
                if (i == count - 1) {
                    int len = fileByte.length - (INPUT_LENGTH_256 * i);
                    branchBuf = ByteBuffer.allocate(len);
                    branchBuf.put(fileByte, INPUT_LENGTH_256 * i, len);
                } else {
                    branchBuf = ByteBuffer.allocate(INPUT_LENGTH_256);
                    branchBuf.put(fileByte, INPUT_LENGTH_256 * i,
                            INPUT_LENGTH_256);
                }
                branchBuf.flip();
                // 8.获取分包的crc16并整合分包
         /*       byte[] branchByte = branchBuf.array();
                byte[] branchByteCRC16 = crc16(branchByte);
                byte[] resultByte = converData(branchByte, branchByteCRC16);
                byteBufferList.add(ByteBuffer.wrap(resultByte));*/
                byteBufferList.add(branchBuf);
                System.out.println("当前是第" + i + "次添加");
            }
        }catch (Exception ex) {
            System.out.println("*********************生成文件分包时出错："
                    + ex.getMessage());
        }
        return byteBufferList;
    }




    /**
     * 获取文件的分包，将文件按1024字节分成若干包
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static List<ByteBuffer> getFile2(Activity activity, String filePath) throws IOException {
        UPDATE_FILE.clear();
        // 从手机本地获取文件
        // 1.定义变量
        List<ByteBuffer> byteBufferList = new ArrayList<ByteBuffer>();
        try {
            InputStream inputStream = activity.getAssets().open(filePath);
            byte[] fileByte = readStreamToByte(inputStream);
            //2.第一个包20个字节
            byte[] firstByte = Arrays.copyOfRange(fileByte, 0, 20);
            ByteBuffer branchBuf1 = ByteBuffer.allocate(20);
            branchBuf1.put(firstByte, 0, 20);
            branchBuf1.flip();
            byteBufferList.add(ByteBuffer.wrap(branchBuf1.array()));
            //3.将剩余数据分包
            byte[] dataByte = Arrays.copyOfRange(fileByte, 20, fileByte.length);
            // 4.获取文件分包次数(第一个包是20个字节)
            int count = (((dataByte.length) % INPUT_LENGTH_1024) == 0) ? ((dataByte.length) / INPUT_LENGTH_1024)
                    : ((dataByte.length) / INPUT_LENGTH_1024 + 1);
            System.out.println("..........................分包次数:" + count);
            // 5.将分包后数据转为ByteBuffer添加到List中
            for (int i = 0; i < count; i++) {
                // 定义分包
                ByteBuffer branchBuf = null;
                if (i == count - 1) {
                    int len = (dataByte.length) - (INPUT_LENGTH_1024 * i);
                    branchBuf = ByteBuffer.allocate(len);
                    branchBuf.put(dataByte, INPUT_LENGTH_1024 * i, len);
                } else {
                    branchBuf = ByteBuffer.allocate(INPUT_LENGTH_1024);
                    branchBuf.put(dataByte, INPUT_LENGTH_1024 * i,
                            INPUT_LENGTH_1024);
                }
                branchBuf.flip();
                // 6.获取分包封装到集合中
                byte[] branchByte = branchBuf.array();
                byteBufferList.add(ByteBuffer.wrap(branchByte));
                System.out.println("当前是第" + i + "次添加");
            }
        }catch (Exception ex) {
            System.out.println("*********************生成文件分包时出错："
                    + ex.getMessage());
        }
        return byteBufferList;
    }



    /**
     * 获取文件的分包，将文件按1024字节分成若干包
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static List<ByteBuffer> getFileByFis(String filePath) throws IOException {
        UPDATE_FILE.clear();
        // 从手机本地获取文件
        File file = new File(filePath);
        // 1.定义变量
        List<ByteBuffer> byteBufferList = new ArrayList<ByteBuffer>();
        FileInputStream fis = null;
        FileChannel channel = null;
        MappedByteBuffer mbb = null;
        try {
            fis = new FileInputStream(file);
            channel = fis.getChannel();
            // 2.读取文件到ByteBuffer
            mbb = channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            System.out.println(".....................文件总长度:" + file.length());
            // 3.将文件转换成byte[]形式
            byte[] fileByte = new byte[(int) file.length()];
            int ind = 0;
            while (mbb.hasRemaining()) {
                fileByte[ind++] = mbb.get(); // 读取数据
            }
            //4.第一包20个字节
            byte[] firstByte = Arrays.copyOfRange(fileByte, 0, 20);
            ByteBuffer branchBuf1 = ByteBuffer.allocate(20);
            branchBuf1.put(firstByte, 0, 20);
            branchBuf1.flip();
            byteBufferList.add(ByteBuffer.wrap(branchBuf1.array()));
            //5.将剩余数据分包
            byte[] dataByte = Arrays.copyOfRange(fileByte, 20, fileByte.length);
            // 6.获取文件分包次数(第一个包是20个字节)
            int count = (((dataByte.length) % INPUT_LENGTH_1024) == 0) ? ((dataByte.length) / INPUT_LENGTH_1024)
                    : ((dataByte.length) / INPUT_LENGTH_1024 + 1);
            System.out.println("..........................分包次数:" + count);
            // 7.将分包后数据转为ByteBuffer添加到List中
            for (int i = 0; i < count; i++) {
                // 定义分包
                ByteBuffer branchBuf = null;
                if (i == count - 1) {
                    int len = (dataByte.length) - (INPUT_LENGTH_1024 * i);
                    branchBuf = ByteBuffer.allocate(len);
                    branchBuf.put(dataByte, INPUT_LENGTH_1024 * i, len);
                } else {
                    branchBuf = ByteBuffer.allocate(INPUT_LENGTH_1024);
                    branchBuf.put(dataByte, INPUT_LENGTH_1024 * i,
                            INPUT_LENGTH_1024);
                }
                branchBuf.flip();
                // 8.获取分包封装到集合中
                byte[] branchByte = branchBuf.array();
                byteBufferList.add(ByteBuffer.wrap(branchByte));
                System.out.println("当前是第" + i + "次添加");
            }
        } finally {
            try {
                channel.close();
                fis.close();
            } catch (Exception ex) {
                System.out.println("*********************生成文件分包时出错："
                        + ex.getMessage());
            }
        }
        return byteBufferList;
    }





    public static byte[] readStreamToByte(InputStream inputStream) throws IOException {
        //创建字节数组输出流 ，用来输出读取到的内容
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //创建读取缓存,大小为1024
        byte[] buffer = new byte[1024];
        //每次读取长度
        int len = 0;
        //开始读取输入流中的文件
        while( (len = inputStream.read(buffer) ) != -1){ //当等于-1说明没有数据可以读取了
            byteArrayOutputStream.write(buffer,0,len); // 把读取的内容写入到输出流中
        }

        //关闭输入流和输出流
        inputStream.close();
        byteArrayOutputStream.close();
        //返回字符串结果
        return byteArrayOutputStream.toByteArray();
    }


    /**
     * 合并两个byte[]
     *
     * @param byte1
     * @param byte2
     * @return
     */
    public static byte[] converData(byte[] byte1, byte[] byte2) {
        int lengthByte1 = byte1.length;
        int lengthByte2 = byte2.length;
        // 生成新的byte数组，长度为byte1与byte2之和
        byte[] byteResult = new byte[lengthByte1 + lengthByte2];
        System.arraycopy(byte1, 0, byteResult, 0, lengthByte1);
        System.arraycopy(byte2, 0, byteResult, lengthByte1, lengthByte2);
        return byteResult;
    }

    /**
     * CRC16 20151023 jehone
     *
     * @param data
     * @return
     */
    public static byte[] crc16(byte[] data) {
        int value = CRC16.calcCrc16(data);
        byte[] src = new byte[2];
        src[0] = (byte) ((value >> 8) & 0xFF);
        src[1] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * CRC32 20151022 jehone
     *
     * @param data
     * @return
     */
    public static byte[] crc32(byte[] data) {
        CRC32 crc32 = new CRC32();
        crc32.update(data);
        long value = crc32.getValue();
        byte[] src = new byte[4];
        src[0] = (byte) ((value >> 24) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }


}
