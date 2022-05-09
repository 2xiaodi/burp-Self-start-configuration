package burp;

import java.io.*;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


public class BurpExtender implements IBurpExtender {
    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    private PrintWriter stdout;

    @Override
    public void registerExtenderCallbacks(final IBurpExtenderCallbacks callbacks) {

        stdout = new PrintWriter(callbacks.getStdout(), true);
        this.callbacks = callbacks;
        helpers = callbacks.getHelpers();
        callbacks.setExtensionName("自动加载");
        String path = this.getClass().getResource("").getPath().substring(6)+"../../load";
//        stdout.println(path);
        try {
            loading(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stdout.println("success");

    }

    public  void loading(String path) throws IOException {
        File file = new File(path);
        File[] fs = file.listFiles();	//遍历path下的文件和目录，放在File数组中
        for(File f:fs) {                    //遍历File[]数组
            if (!f.isDirectory()) {
                JSONObject job = JSON.parseObject(readJsonFile(f.getCanonicalPath()));
                // stdout.println("--"+f.getAbsolutePath()+"\n--"+f.getCanonicalPath());     // 两种路径比较
                // --D:\Tool\BurpSuiteCommunity\burpsuite_community.jar!\burp\..\..\load\proxy.json
                // --D:\Tool\BurpSuiteCommunity\load\proxy.json
                callbacks.loadConfigFromJson(job.toString());
                stdout.println("Deal with:"+f.getCanonicalPath());
            }
        }
    }


    public static String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile),"utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    }

