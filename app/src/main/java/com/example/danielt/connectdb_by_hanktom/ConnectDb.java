package com.example.danielt.connectdb_by_hanktom;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by HWT49 on 2016/6/20.
 */
public class ConnectDb {

    String myURL;

    URL url;

    Uri.Builder builder;

    final String CREATE_USER = "http://218.161.15.149:8080/AndroidConnectDB/create_user.php";

    final String LOGIN = "http://218.161.15.149:8080/AndroidConnectDB/login.php";

    final String SENT_VERIFICATION_CODE = "http://218.161.15.149:8080/AndroidConnectDB/sent_verification_code.php";

    final String CREATE_DIARY = "http://218.161.15.149:8080/AndroidConnectDB/create_diary.php";

    final String LOAD_DIARY = "http://218.161.15.149:8080/AndroidConnectDB/load_diary.php";

    final String CHECK_FRIEND_INVITATION = "http://218.161.15.149:8080/AndroidConnectDB/check_friend_invitation.php";

    final String CREATE_A_FRIEND = "http://218.161.15.149:8080/AndroidConnectDB/create_a_friend.php";

    final String CREATE_FRIEND_INVITATION = "http://218.161.15.149:8080/AndroidConnectDB/create_friend_invitation.php";

    final String LOAD_ALL_DIARY = "http://218.161.15.149:8080/AndroidConnectDB/load_all_diary.php";

    final String LOAD_FRIEND_LIST = "http://218.161.15.149:8080/AndroidConnectDB/load_friend_list.php";

    final String SAVE_PERSONAL_INFO = "http://218.161.15.149:8080/AndroidConnectDB/save_personal_info.php";

    final String LOAD_PERSONAL_INFO = "http://218.161.15.149:8080/AndroidConnectDB/load_personal_info.php";

    User myUser;

    Login myLogin;

    Diary myDiary;

    Friend_invitation friend_invitation;

    Message message;

    All_diary all_diary;

    All_diary[] diary_array;

    Friend_list friend_list;

    String[] request_data_array;

    Personal_info personal_info;

    //　URL　是要進行互動的　PHP　檔案位址，　request_data　是使用者要新增的資料
    public Object askConnect(final String URL, String request_data) {

        request_data_array = request_data.split("\n");

        //連線的程式碼要放在try catch內，才不會發生error

        try {

            myURL = URL;

            switch (myURL) {

                case CREATE_USER: {

                    //要進行互動的Url位址

                    url = new URL(CREATE_USER);

                    //要傳給互動位址的內容

                    builder = new Uri.Builder()

                            //account是標籤，request_data_array[0]是從參數request_data取出的資料，代表account

                            //在PHP中，$_POST['account']即代表test6

                            .appendQueryParameter("account", request_data_array[0])

                            .appendQueryParameter("password", request_data_array[1]);

                    break;

                }

                case LOGIN: {

                    //要進行互動的Url位址

                    url = new URL(LOGIN);

                    //要傳給互動位址的內容

                    builder = new Uri.Builder()

                            //account是標籤，request_data_array[0]是從參數request_data取出的資料，代表account

                            //在PHP中，$_POST['account']即代表test6

                            .appendQueryParameter("account", request_data_array[0])

                            .appendQueryParameter("password", request_data_array[1])

                            .appendQueryParameter("last_login", request_data_array[2]);

                    break;

                }

                case SENT_VERIFICATION_CODE: {

                    //要進行互動的Url位址

                    url = new URL(SENT_VERIFICATION_CODE);

                    //要傳給互動位址的內容

                    builder = new Uri.Builder()

                            .appendQueryParameter("target_email", request_data_array[0])

                            .appendQueryParameter("verification_code", request_data_array[1]);

                    break;

                }

                case CREATE_DIARY: {

                    request_data_array = request_data.split("\r");

                    //要進行互動的Url位址

                    url = new URL(CREATE_DIARY);

                    //要傳給互動位址的內容

                    builder = new Uri.Builder()

                            .appendQueryParameter("uId", request_data_array[0])

                            .appendQueryParameter("date", request_data_array[1])

                            .appendQueryParameter("title", request_data_array[2])

                            .appendQueryParameter("mark", request_data_array[3])

                            .appendQueryParameter("mood", request_data_array[4])

                            .appendQueryParameter("diary_text", request_data_array[5])

                            .appendQueryParameter("pic", request_data_array[6]);

                    Log.d("create_diary", "1");

                    break;

                }

                case LOAD_DIARY: {

                    //要進行互動的Url位址

                    url = new URL(LOAD_DIARY);

                    //要傳給互動位址的內容

                    builder = new Uri.Builder()

                            .appendQueryParameter("uId", request_data_array[0])

                            .appendQueryParameter("date", request_data_array[1]);

                    Log.d("load_diary", "prepare request");

                    break;

                }

                case LOAD_ALL_DIARY: {

                    //要進行互動的Url位址

                    url = new URL(LOAD_ALL_DIARY);

                    //要傳給互動位址的內容

                    builder = new Uri.Builder()

                            .appendQueryParameter("uId", request_data_array[0]);

                    Log.d("load_diary", "prepare request");

                    break;

                }

                case CHECK_FRIEND_INVITATION: {

                    url = new URL(CHECK_FRIEND_INVITATION); //要進行互動的Url位址

                    builder = new Uri.Builder() //要傳給互動位址的內容

                            .appendQueryParameter("uId", request_data_array[0]);

                    Log.d("CHECK_FRIEND_INVITATION", "prepare request");

                    break;

                }
                case CREATE_A_FRIEND: {

                    url = new URL(CREATE_A_FRIEND); //要進行互動的Url位址

                    builder = new Uri.Builder() //要傳給互動位址的內容

                            .appendQueryParameter("uId", request_data_array[0])

                            .appendQueryParameter("fId", request_data_array[1])

                            .appendQueryParameter("authority_level", request_data_array[2])

                            .appendQueryParameter("friend_date", request_data_array[3]);

                    Log.d("CHECK_FRIEND_INVITATION", "prepare request");

                    break;

                }

                case CREATE_FRIEND_INVITATION: {

                    //要進行互動的Url位址

                    url = new URL(CREATE_FRIEND_INVITATION);

                    //要傳給互動位址的內容

                    builder = new Uri.Builder()

                            .appendQueryParameter("uId", request_data_array[0])

                            .appendQueryParameter("target_id", request_data_array[1])

                            .appendQueryParameter("invitation_date", request_data_array[2]);

                    Log.d("CHECK_FRIEND_INVITATION", "prepare request");

                    break;

                }

                case LOAD_FRIEND_LIST: {

                    //要進行互動的Url位址

                    url = new URL(LOAD_FRIEND_LIST);

                    //要傳給互動位址的內容

                    builder = new Uri.Builder()

                            .appendQueryParameter("uId", request_data_array[0]);

                    Log.d("LOAD_FRIEND_LIST", "prepare request");

                    break;

                }

                case LOAD_PERSONAL_INFO: {

                    //要進行互動的Url位址

                    url = new URL(LOAD_PERSONAL_INFO);

                    //要傳給互動位址的內容

                    builder = new Uri.Builder()

                            .appendQueryParameter("uId", request_data_array[0]);

                    Log.d("LOAD_PERSONAL_INFO", "prepare request");

                    break;

                }

                case SAVE_PERSONAL_INFO: {

                    request_data_array = request_data.split("\r");

                    //要進行互動的Url位址

                    url = new URL(SAVE_PERSONAL_INFO);

                    //要傳給互動位址的內容

                    builder = new Uri.Builder()

                            .appendQueryParameter("uId", request_data_array[0])

                            .appendQueryParameter("full_name", request_data_array[1])

                            .appendQueryParameter("constellation", request_data_array[2])

                            .appendQueryParameter("phone_number", request_data_array[3])

                            .appendQueryParameter("address", request_data_array[4])

                            .appendQueryParameter("birth_date", request_data_array[5])

                            .appendQueryParameter("personal_message", request_data_array[6]);

                    Log.d("LOAD_PERSONAL_INFO", "prepare request");

                    break;

                }

                //這裡新增其他的case

            }

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setReadTimeout(9000);

            urlConnection.setConnectTimeout(9000);

            String query = builder.build().getEncodedQuery();

            //設定互動的方式

            urlConnection.setRequestMethod("POST");

            urlConnection.setDoInput(true);

            urlConnection.setDoOutput(true);

            urlConnection.connect();

            //Send request

            DataOutputStream wr = new DataOutputStream(

                    urlConnection.getOutputStream());

            wr.writeBytes(query);

            //清除用戶端資料緩存
            wr.flush();

            wr.close();


            Log.d("getResponseMessage()", urlConnection.getResponseMessage() + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

            //Get Response
            InputStream isNew = urlConnection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(isNew));

            String line;

            StringBuffer response = new StringBuffer();

            while ((line = rd.readLine()) != null) {

                response.append(line);

            }

            rd.close();

            System.out.println("Final response=" + response.toString());

            //使用parseJSON，解析回傳的資料
//            parseJSON(response.toString());

            switch (URL) {

                case CREATE_USER: {

                    myUser = new User(response.toString());

                    return myUser;

                }

                case LOGIN: {

                    myLogin = new Login(response.toString());

                    return myLogin;

                }

                case SENT_VERIFICATION_CODE: {

                    message = new Message(response.toString());

                    return message;

                }

                case CREATE_DIARY: {

                    myDiary = new Diary(response.toString());

                    return myDiary;

                }

                case LOAD_DIARY: {

                    myDiary = new Diary(response.toString());

                    return myDiary;

                }

                case LOAD_ALL_DIARY: {

                    //建立all_diary物件

                    all_diary = new All_diary(response.toString());

                    //得到存放日記物件的陣列

                    diary_array = all_diary.getDiary_array();

                    return diary_array;

                }

                case CHECK_FRIEND_INVITATION: {

                    //把response回的資料轉成字串，傳給隸屬的物件當參數

                    //傳回的資料即已轉成該物件應有的格式，可以使用get...取資料

                    friend_invitation = new Friend_invitation(response.toString());

                    return friend_invitation;

                }

                case CREATE_A_FRIEND: {

                    message = new Message(response.toString());

                    return message;

                }

                case CREATE_FRIEND_INVITATION: {

                    message = new Message(response.toString());

                    return message;

                }

                case LOAD_FRIEND_LIST: {

                    friend_list = new Friend_list(response.toString());

                    return friend_list;

                }

                case LOAD_PERSONAL_INFO: {

                    personal_info = new Personal_info(response.toString());

                    return personal_info;

                }

                case SAVE_PERSONAL_INFO: {

                    message = new Message(response.toString());

                    return message;

                }
            }
            //URL格式錯誤



        }
        catch (Exception e){

            e.printStackTrace();

        }
//        catch (MalformedURLException e) {
//
//            e.printStackTrace();
//
//        } catch (SocketTimeoutException e) {
//
//            e.printStackTrace();
//
//        } catch (IOException e) {
//
//            e.printStackTrace();
//
//        }

        return null;
    }
}
