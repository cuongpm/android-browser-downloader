package core.common;

public class Constant {

    // App data
    public final static String SEARCH_URL = "https://www.google.com/search?tbm=vid&q=%s -youtube";

    // Remote data
    public final static String REMOTE_URL = "https://generaldata-79d9b.firebaseapp.com/api/videodownloader/";

    // Parser server
    public final static String URL_SERVER_1 = "http://youtube-dl55.herokuapp.com/api/info?url=%s";
    public final static String URL_SERVER_2 = "http://youtube-dl99.herokuapp.com/api/info?url=%s";

    // App config
    public final static String UA_ID = "UA-102761433-1";

    public final static String AD_APP_ID = "ca-app-pub-9480296373139891~2380509503";
    public final static String AD_BANNER_ID = "ca-app-pub-9480296373139891/5668963921";
    public final static String AD_INTERSTITIAL_ID = "ca-app-pub-9480296373139891/2707812565";

    // get video data script
    public static String VIDEO_SCRIPT = "javascript:function clickOnVideo(link, name, classValueName){" +
            "browser.getVideoData(link, name);" +
            "var element = document.getElementById(\"mInlineVideoPlayer\");" +
            "element.muted = true;" +
            "var parent = element.parentNode; " +
            "parent.removeChild(element);" +
            "parent.setAttribute('class', classValueName);}" +
            "function getVideoLink(){" +
            "try{var items = document.getElementsByTagName(\"div\");" +
            "for(i = 0; i < items.length; i++){" +
            "if(items[i].getAttribute(\"data-sigil\") == \"inlineVideo\"){" +
            "var classValueName = items[i].getAttribute(\"class\");" +
            "var jsonString = items[i].getAttribute(\"data-store\");" +
            "var obj = JSON && JSON.parse(jsonString) || $.parseJSON(jsonString);" +
            "var videoLink = obj.src;" +
            "var videoName = obj.videoID;" +
            "items[i].setAttribute('onclick', \"clickOnVideo('\"+videoLink+\"','\"+videoName+\"','\"+classValueName+\"')\");}}" +
            "var links = document.getElementsByTagName(\"a\");" +
            "for(i = 0; i < links.length; i++){" +
            "if(links[ i ].hasAttribute(\"data-store\")){" +
            "var jsonString = links[i].getAttribute(\"data-store\");" +
            "var obj = JSON && JSON.parse(jsonString) || $.parseJSON(jsonString);" +
            "var videoName = obj.videoID;" +
            "var videoLink = links[i].getAttribute(\"href\");" +
            "var res = videoLink.split(\"src=\");" +
            "var myLink = res[1];" +
            "links[i].parentNode.setAttribute('onclick', \"browser.getVideoData('\"+myLink+\"','\"+videoName+\"')\");" +
            "while (links[i].firstChild){" +
            "links[i].parentNode.insertBefore(links[i].firstChild," +
            "links[i]);}" +
            "links[i].parentNode.removeChild(links[i]);}}}catch(e){}}" +
            "getVideoLink();";
}
