package com.scriptchess.utility;


import com.scriptchess.exception.NetworkException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.HttpClients;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 13/10/22
 */


public class Request {
    /**
     * Needs to be passed to API for identify user
     */
    private static final String USER_ID_HEADER = "X-USER-ID";
    /**
     * Needs to be passed to API to authorize user
     **/
    private static final String ACCESS_TOKEN_HEADER = "Authorization";

    /**
     * Will be used for future reference
     */
    private static final String DATE_TOKEN_HEADER = "X-DATE-TOKEN";

    /**
     * A session token to uniquely identify current session
     */
    private static final String APP_SESSION = "X-SESSION";

    /**
     * version of the app
     */
    private static final String APP_VERSION = "X-VERSION";
    private static final int UNAUTHORIZED_CODE = 401;

    public final String AUTHORIZATION = "x-api-key";

    private static Request instance;
    private static Map<String, String> mandatoryHeaders;


    private static final int SUCCESS_CODE = 200;
    private static final int FAILURE_CODE = 400;
    private String userName = "";
    private String password = "";
    private Request() {
        this(false);
    }
    private Request(boolean noMandatoryHeader) {
        if(!noMandatoryHeader) {
            setMandatoryHeaders();
        }
    }
    public static Request getInstance() {
        if(instance != null)
            return instance;
        else
            instance = new Request();
        return instance;
    }

    public static Request getInstance(boolean noMandatoryHeader) {
        if(instance != null)
            return instance;
        else
            instance = new Request(noMandatoryHeader);
        return instance;
    }

    /**
     * Sets mandatory headers for consuming Pixyfi services
     */
    public void setMandatoryHeaders() {
        mandatoryHeaders = new HashMap<String, String>();
        //mandatoryHeaders.put(AUTHORIZATION, bearerToken);
        mandatoryHeaders.put("User-Agent","PostmanRuntime/7.26.8");
    }

    public static void clearMandatoryHeaders() {
        if(mandatoryHeaders != null)
            mandatoryHeaders.clear();
    }

    /**
     * Generates a unique UUID
     * @return a unique UUID
     */
    private static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Spawns a GET request
     * @param url url of the service to be consumed
     * @param headers any headers apart from mandatory headers
     * @return
     */
    public String doGet(String url, Map<String, String> headers)
        throws NetworkException {
        String response = doGet(url, headers, true);
        return response;
    }

    /**
     * Spawns a GET request
     * @param url url of the service to be consumed
     * @param headers any headers apart from mandatory headers
     * @param includeMandatoryHeaders whether or not include mandatory headers
     * @return
     */
    public String doGet(String url, Map<String, String> headers, boolean includeMandatoryHeaders)
        throws NetworkException {
        String response = sendGetRequest(url, headers, includeMandatoryHeaders);
        return response;
    }

    public String sendGetRequest(String url, Map<String, String> headers,
                                 boolean includeMandatoryHeaders) throws NetworkException {
        return sendGetRequestWithFullResponse(url, headers, includeMandatoryHeaders).getResponse();
    }
    public Response sendGetRequestWithFullResponse(String url, Map<String, String> headers,
                                                   boolean includeMandatoryHeaders) throws NetworkException {
        Response fullResponse = new Response();
        HttpURLConnection connection = null;
        String response = null;
        URL restUrl = null;
        BufferedWriter writer = null;
        BufferedReader br = null;
        String body = null;
        Map<String, String> allHeaders = new HashMap<String, String>();
        if(headers != null) {
            allHeaders.putAll(headers);
        }
        if(includeMandatoryHeaders && mandatoryHeaders != null) {
            allHeaders.putAll(mandatoryHeaders);
        }
        try {
            restUrl = new URL(url);
            connection = (HttpURLConnection) restUrl.openConnection();
            connection.setRequestMethod("GET");
            if(allHeaders != null) {
                for (String key : allHeaders.keySet()) {
                    connection.setRequestProperty(key,  allHeaders.get(key));
                }
            }

            fullResponse.setResponseHeaders(connection.getHeaderFields());
            if(connection.getResponseCode() >= FAILURE_CODE) {
                response = convertStreamToString(connection.getInputStream());
                response = getErrorString(response);
                throw new NetworkException(response, connection.getResponseCode());
            }
            else {
                response = convertStreamToString(connection.getInputStream());
            }

        } catch (MalformedURLException e) {
            try {
                String error = convertStreamToString(connection.getErrorStream());
                throw new NetworkException(error, connection.getResponseCode());
            } catch (IOException e1) {
                throw new NetworkException("Unexpected error Occurred", 600);
            }
        } catch (IOException e) {
            try {
                String error = convertStreamToString(connection.getErrorStream());
                throw new NetworkException(error);
            } catch (IOException e1) {
                throw new NetworkException("Unexpected error Occurred", 600);
            }
        }
        finally {
            if(writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    //throw new NetworkException("Unexpected error Occurred", 600);
                }
            }
            if(br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    //throw new NetworkException("Unexpected error Occurred", 600);
                }
            }
        }
        fullResponse.setResponse(response);
        return fullResponse;
    }
    public BufferedImage multipartRequest(String url, Map<String, String> headers,
                                          Map<String, Object> params) {
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create().setCharset(Charset.forName("UTF-8"));
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if(param.getValue() instanceof Multipart) {
                multipartEntityBuilder = multipartEntityBuilder.addPart(param.getKey(),
                    new ByteArrayBody(((Multipart)param.getValue()).getData(),
                        ((Multipart)param.getValue()).getName()));
            } else {
                multipartEntityBuilder.addTextBody(param.getKey(), param.getValue().toString());
            }

        }
        HttpEntity httpEntity = multipartEntityBuilder.build();

        HttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost(url);
        for(Map.Entry<String, String> header : mandatoryHeaders.entrySet()) {
            request.setHeader(header.getKey(), header.getValue());
        }
        request.setEntity(httpEntity);
        try {
            HttpResponse response = httpClient.execute(request);
            if(response.getFirstHeader("Content-Type").getValue().equals("image/png")) {
                InputStream content = response.getEntity().getContent();
                return ImageIO.read(content);
            } else {
                InputStream content = response.getEntity().getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(content));
                String st = br.readLine();
                while (st!= null || st.trim().length() >0) {
                    System.out.println(st);
                    st = br.readLine();
                }
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }


    /**
     * Sends a multipart POST request. Use this if you have to send a binary data as part of form
     * @param url
     * @param headers
     * @param param
     * @return
     * @throws NetworkException
     */
    public String doMultipartPostRequest(String url, Map<String, String> headers,
                                         Map<String, Object> param) throws NetworkException {
        String delimiter ="--";
        String boundary = "SwA"+ Long.toString(System.currentTimeMillis())+"SwA";
        HttpURLConnection con = null;
        OutputStream os = null;
        BufferedReader br = null;
        String response = null;
        try {
            con = (HttpURLConnection) (new URL(url)).openConnection();
            con.setRequestMethod("POST");
            Map<String, String> allHeaders = new HashMap<>();
            if(headers != null) {
                allHeaders.putAll(headers);
            }
            if(mandatoryHeaders != null) {
                allHeaders.putAll(mandatoryHeaders);
            }


            con.setDoInput(true);
            con.setDoOutput(true);
            con.setConnectTimeout(600*1000);
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            if(allHeaders != null) {
                for (String key : allHeaders.keySet()) {
                    con.setRequestProperty(key,  allHeaders.get(key));
                }
            }
            con.connect();
            os = con.getOutputStream();
            if(param != null) {
                for (String key : param.keySet()) {
                    if(param.get(key) != null) {
                        if (param.get(key) instanceof String) {
                            addFormPart(delimiter, boundary, key, param.get(key).toString(), os);
                        } else {
                            if (param.get(key) instanceof List) {
                                List<byte[]> files = (List) param.get(key);
                                for (byte[] file : files) {
                                    addFilePart(delimiter, boundary, key, "image", file, os);
                                }
                            } else {
                                if(param.get(key) instanceof MultipartData) {
                                    MultipartData data = (MultipartData) param.get(key);
                                    addFilePart(delimiter, boundary, key, data.getName(),
                                        data.getData(), os);
                                }

                            }
                        }
                    }
                }
            }
            os.write( (delimiter + boundary + delimiter + "\r\n").getBytes());

            if(con.getResponseCode() >= FAILURE_CODE) {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                response = br.readLine();
                response = getErrorString(response);
                throw new NetworkException(response);
            }
            else {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                response = br.readLine();
            }
        }
        catch(MalformedURLException x) {
            String error = response == null?x.getLocalizedMessage():response;
            throw new NetworkException(error);
        }
        catch (IOException e) {
            String error = response == null?e.getLocalizedMessage():response;
            try {
                int errorCode = con.getResponseCode();
                throw new NetworkException(error, errorCode);
            } catch (IOException e1) {

                throw new RuntimeException(e1);
            }

        }
        finally {
            if(os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    //throw new RuntimeException(e);
                }
            }
            if(br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    //throw new RuntimeException(e);
                }
            }
        }
        return response;
    }

    /**
     * Sends a multipart POST request. Use this if you have to send a binary data as part of form
     * @param url
     * @param headers
     * @param param
     * @return
     * @throws NetworkException
     */
    public String doMultipartPatchRequest(String url, Map<String, String> headers,
                                          Map<String, Object> param) throws NetworkException {
        String delimiter ="--";
        String boundary = "SwA"+ Long.toString(System.currentTimeMillis())+"SwA";
        HttpURLConnection con = null;
        OutputStream os = null;
        BufferedReader br = null;
        String response = null;
        try {
            con = (HttpURLConnection) (new URL(url)).openConnection();
            con.setRequestMethod("PATCH");
            Map<String, String> allHeaders = new HashMap<>();
            if(headers != null) {
                allHeaders.putAll(headers);
            }
            if(mandatoryHeaders != null) {
                allHeaders.putAll(mandatoryHeaders);
            }


            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            if(allHeaders != null) {
                for (String key : allHeaders.keySet()) {
                    con.setRequestProperty(key,  allHeaders.get(key));
                }
            }
            con.connect();
            os = con.getOutputStream();
            if(param != null) {
                for (String key : param.keySet()) {
                    if(param.get(key) != null) {
                        if (param.get(key) instanceof String) {
                            addFormPart(delimiter, boundary, key, param.get(key).toString(), os);
                        } else {
                            if (param.get(key) instanceof List) {
                                List<byte[]> files = (List) param.get(key);
                                for (byte[] file : files) {
                                    addFilePart(delimiter, boundary, key, "image", file, os);
                                }
                            } else {
                                addFilePart(delimiter, boundary, key, "image",
                                    (byte[]) param.get(key), os);
                            }
                        }
                    }
                }
            }
            os.write( (delimiter + boundary + delimiter + "\r\n").getBytes());

            if(con.getResponseCode() >= FAILURE_CODE) {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                response = br.readLine();
                response = getErrorString(response);
                throw new NetworkException(response);
            }
            else {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                response = br.readLine();
            }
        }
        catch(MalformedURLException x) {
            String error = response == null?x.getLocalizedMessage():response;
            throw new NetworkException(error);
        }
        catch (IOException e) {
            String error = response == null?e.getLocalizedMessage():response;
            try {
                int errorCode = con.getResponseCode();
                throw new NetworkException(error, errorCode);
            } catch (IOException e1) {
                throw new NetworkException("Unexpected error Occurred", 600);
            }

        }
        finally {
            if(os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    //throw new NetworkException("Unexpected error Occurred", 600);
                }
            }
            if(br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    //throw new NetworkException("Unexpected error Occurred", 600);
                }
            }
        }
        return response;
    }

    private static void addFormPart(String delimiter, String boundary,
                                    String paramName, String value, OutputStream os)
        throws IOException {
        writeParamData(delimiter, boundary, paramName, value, os);
    }

    private static void writeParamData(String delimiter, String boundary, String paramName,
                                       String value, OutputStream os) throws IOException {
        os.write( (delimiter + boundary + "\r\n").getBytes());
        os.write( "Content-Type: text/plain\r\n".getBytes());
        os.write( ("Content-Disposition: form-data; name=\"" + paramName + "\"\r\n").getBytes());;
        os.write( ("\r\n" + value + "\r\n").getBytes());
    }

    private static void addFilePart(String delimiter, String boundary, String paramName,
                                    String fileName, byte[] data, OutputStream os)
        throws IOException {
        os.write( (delimiter + boundary + "\r\n").getBytes());
        os.write( ("Content-Disposition: form-data; name=\"" + paramName +
            "\"; filename=\"" + fileName + "\"\r\n"  ).getBytes());
        os.write( ("Content-Type: application/octet-stream\r\n"  ).getBytes());
        os.write( ("Content-Transfer-Encoding: binary\r\n"  ).getBytes());
        os.write("\r\n".getBytes());

        os.write(data);

        os.write("\r\n".getBytes());
    }

    /**
     * Send POST request to given URL
     * @param url
     * @param data
     * @param headers
     * @return
     * @throws NetworkException
     */
    public String doPost(String url, String data, Map<String, String> headers)
        throws NetworkException {
        return completeRequest("POST", url, data, headers);
    }

    /**
     * Send PUT request to given URL
     * @param url
     * @param data
     * @param headers
     * @return
     * @throws NetworkException
     */
    public String doPut(String url, String data, Map<String, String> headers)
        throws NetworkException {
        return completeRequest("PUT", url, data, headers);
    }

    /**
     * Send POST request to given URL
     * @param url
     * @param data
     * @param headers
     * @return
     * @throws NetworkException
     */
    public String doDelete(String url, String data, Map<String, String> headers)
        throws NetworkException {
        return completeRequest("DELETE", url, data, headers);
    }

    /**
     * Generic method for completing an http request with body
     * @param method
     * @param url
     * @param data
     * @param headers
     * @return
     * @throws NetworkException
     */
    private String completeRequest(String method, String url, String data,
                                   Map<String, String> headers) throws NetworkException {
        HttpURLConnection connection = null;
        String response = null;
        URL restUrl = null;
        OutputStream os = null;
        BufferedWriter writer = null;
        BufferedReader br = null;
        String body = null;
        Map<String, String> allHeaders = new HashMap<>();
        if(headers != null) {
            allHeaders.putAll(headers);
        }
        if(mandatoryHeaders != null) {
            allHeaders.putAll(mandatoryHeaders);
        }
        allHeaders.put("content-type","application/json");
        try {
            restUrl = new URL(url);
            connection = (HttpURLConnection) restUrl.openConnection();
            switch (method) {
                case "POST":
                    connection.setRequestMethod("POST");
                    break;
                case "PUT":
                    connection.setRequestMethod("PUT");
                    break;
                case "DELETE":
                    connection.setRequestMethod("DELETE");
                    break;
            }
            if(allHeaders != null) {
                for (String key : allHeaders.keySet()) {
                    connection.setRequestProperty(key,  allHeaders.get(key));
                }
            }
            connection.setDoOutput(true);
            connection.setConnectTimeout(600*1000);
            os = connection.getOutputStream();
            writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            if(!Strings.isNullOrEmpty(data)) {
                body = data;
                writer.write(body);
                writer.flush();
            }

            if(connection.getResponseCode() >= FAILURE_CODE) {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                response = br.readLine();
                response = getErrorString(response);
                throw new NetworkException(response, connection.getResponseCode());
            }
            else {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                response = br.readLine();
            }

        } catch (MalformedURLException e) {
            String error = "Incorrect Url provided Used";
            throw new NetworkException(error);
        } catch (IOException e) {
            try {
                String error = convertStreamToString(connection.getErrorStream());
                int errorCode = connection.getResponseCode();
                throw new NetworkException(error, errorCode);
            } catch (IOException e1) {
                throw new NetworkException("Unexpected error Occurred", 600);
            }
        }
        finally {
            if(writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    //throw new NetworkException("Unexpected error Occurred", 600);
                }
            }

            if(os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    //throw new NetworkException("Unexpected error Occurred", 600);
                }
            }
            if(br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    //throw new NetworkException("Unexpected error Occurred", 600);
                }
            }
        }
        return response;
    }






    private static String getPostDataString(Map<String, String> params) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        if(params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");
                try {
                    result.append(URLEncoder.encode(entry.getKey() == null ? "" : entry.getKey(), "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(entry.getValue() == null ? "" : entry.getValue(), "UTF-8"));
                } catch (UnsupportedEncodingException x) {
                    System.err.println(x.getMessage());
                }
            }
        }
        return result.toString();
    }

    private static String convertStreamToString(InputStream is) throws IOException, NetworkException {
        if(is ==  null) {
            throw new NetworkException("Unable to connect to pixyfi at this moment");
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                //throw e;
            }
        }

        return sb.toString();
    }

    private static String getErrorString(String string) {
        return string;
    }

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static class Response {
        private String response;
        private Map<String, List<String>> responseHeaders;

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }

        public Map<String, List<String>> getResponseHeaders() {
            return responseHeaders;
        }

        public void setResponseHeaders(Map<String, List<String>> responseHeaders) {
            this.responseHeaders = responseHeaders;
        }
    }

    public static byte[] readImage(String path) throws IOException {
        BufferedImage bImage = ImageIO.read(new File(path));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "webp", bos );
        return bos.toByteArray();
    }



    public static class MultipartData {
        private String name;
        private byte[] data;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }
    }

    public static class Multipart {
        private byte[] data;
        private String name;

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

