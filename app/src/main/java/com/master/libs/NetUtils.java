package com.master.libs;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by 不听话的好孩子 on 2018/3/28.
 */

public class NetUtils {
    public static int LISTENER_PERIOD=500;

    public static String get(String url) {
        return get(url, null);
    }
    public static String get(String url, Params params) {
        try {
            if (params != null) {
                if (!url.endsWith("?")) {
                    url = url + "?";
                }
                url = url + params.toParamsString();
            }
            URL urlx = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlx.openConnection();

            if(params!=null) {
                for (Map.Entry<String, String> entry : params.headers.entrySet()) {
                    connection.addRequestProperty(entry.getKey(),entry.getValue());
                }
            }

            if (connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();
                String str = null;
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                reader.close();
                connection.disconnect();
                return buffer.toString();
            } else {
                System.out.println(connection.getResponseCode() + "访问错误");
            }
            connection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String post(String url, Params params) {
        try {
            URL urlx = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlx.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            if(params!=null) {
                for (Map.Entry<String, String> entry : params.headers.entrySet()) {
                    connection.addRequestProperty(entry.getKey(),entry.getValue());
                }
                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.write(params.toParamsString().getBytes());
                outputStream.flush();
                outputStream.close();
            }
            if (connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();
                String str = null;
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                reader.close();
                connection.disconnect();
                return buffer.toString();
            }
            connection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String upload(String url, Params params, ProgressListener listener) {
        String boundary = "*****";
        String twoHyphens = "--";
        String end = "\r\n";
        try {
            URL urlx = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlx.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            if(params!=null) {
                for (Map.Entry<String, String> entry : params.headers.entrySet()) {
                    connection.addRequestProperty(entry.getKey(),entry.getValue());
                }
            }
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            if(params!=null) {
                Set<Map.Entry<String, String>> entries = params.files.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    String keyname = entry.getKey();
                    File file = new File(entry.getValue());
                    if (file.exists()) {
                        outputStream.writeBytes(twoHyphens + boundary + end);
                        outputStream.writeBytes("Content-Disposition: form-data; " + "name=\"" + keyname + "\";filename=\"" + file.getName()
                                + "\"" + end);
                        outputStream.writeBytes("Content-Type: application/octet-stream; charset=utf-8"+end);
                        outputStream.writeBytes(end);
                        FileInputStream fStream = new FileInputStream(file);
                        int bufferSize = 2048;
                        byte[] buffer = new byte[bufferSize];
                        long total = fStream.available();
                        long current = 0;
                        long last = 0;
                        int length = -1;
                        long lastTime = System.currentTimeMillis();
                        while ((length = fStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, length);
                            current += length;
                            long temp = System.currentTimeMillis();
                            if (temp - lastTime > LISTENER_PERIOD || current == total) {
                                if (listener != null) {
                                    listener.call(current, total,(int)((current-last)/(temp - last)), keyname);
                                }
                                lastTime = temp;
                                last = current;
                            }
                        }
                        fStream.close();
                    }
                }
            }
            if (params != null) {
                for (Map.Entry<String, String> entry : params.params.entrySet()) {
                    outputStream.writeBytes(twoHyphens + boundary + end);
                    outputStream.writeBytes("Content-Disposition: form-data; " + "name=\"" + entry.getKey()
                            + "\"" + end);
                    outputStream.writeBytes("Content-Type: text/plain; charset=utf-8"+end);
                    outputStream.write(end.getBytes());
                    outputStream.write(entry.getValue().getBytes());
                    outputStream.write(end.getBytes());
                }
            }
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + end);
            outputStream.flush();
            outputStream.close();
            if (connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();
                String str = null;
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                reader.close();
                connection.disconnect();
                return buffer.toString();
            } else {
                System.out.println(connection.getResponseCode() + "访问错误");
            }
            connection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void download(String url, File file, ProgressListener listener) {
        try {
            URL urlx = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlx.openConnection();
            if (connection.getResponseCode() == 200) {

                File parentFile = file.getParentFile();
                if (parentFile != null && !parentFile.exists()) {
                    parentFile.mkdirs();
                }

                InputStream inputStream = connection.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(file);

                long total = connection.getContentLength();
                long current = 0;
                long last = 0;

                byte[] bytes = new byte[2048];
                int read = 0;
                long lastTime = System.currentTimeMillis();
                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                    current += read;
                    long temp = System.currentTimeMillis();
                    if(temp - lastTime > LISTENER_PERIOD || current == total) {
                        if (listener != null) {
                            listener.call(current, total,(int)((current-last)/(temp - lastTime)), file.getName());
                        }
                        lastTime=temp;
                        last=current;
                    }
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
            } else {
                System.out.println(connection.getResponseCode() + "访问错误");
            }
            connection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static interface ProgressListener {
        void call(long current, long total, int speed, String keyname);
    }

    public static class Params{
        private LinkedHashMap<String, String> params =new LinkedHashMap();
        private LinkedHashMap<String, String> headers =new LinkedHashMap();
        private LinkedHashMap<String, String> files =new LinkedHashMap();
        public Params add(String key) {
            params.put(key, "");
            return this;
        }

        public Params add(String key, int value) {
            params.put(key, value + "");
            return this;
        }

        public Params add(String key, Object object) {
            params.put(key, object == null ? "" : object.toString());
            return this;
        }
        public Params addHeader(String key) {
            headers.put(key, "");
            return this;
        }

        public Params addHeader(String key, int value) {
            headers.put(key, value + "");
            return this;
        }

        public Params addHeader(String key, Object object) {
            headers.put(key, object == null ? "" : object.toString());
            return this;
        }

        public Params addFile(String key, String value) {
            files.put(key, value + "");
            return this;
        }

        public Params addFile(String key, Object object) {
            files.put(key, object == null ? "" : object.toString());
            return this;
        }

        private String toParamsString() {
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<String, String> stringStringEntry : params.entrySet()) {
                builder.append("&");
                builder.append(stringStringEntry.getKey());
                builder.append("=");
                try {
                    builder.append(URLEncoder.encode(stringStringEntry.getValue(), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
            String paramsStr = builder.toString();
            if (paramsStr.startsWith("&")) {
                paramsStr = paramsStr.substring(1);
            }
            return paramsStr;
        }

    }
}
