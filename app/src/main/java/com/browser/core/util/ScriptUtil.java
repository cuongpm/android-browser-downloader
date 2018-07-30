package com.browser.core.util;

public class ScriptUtil {

    public static String FACEBOOK_SCRIPT = "javascript:function clickOnVideo(link, classValueName){" +
            "browser.getVideoData(link);" +
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
            "items[i].setAttribute('onclick', \"clickOnVideo('\"+videoLink+\"','\"+classValueName+\"')\");}}" +
            "var links = document.getElementsByTagName(\"a\");" +
            "for(i = 0; i < links.length; i++){" +
            "if(links[ i ].hasAttribute(\"data-store\")){" +
            "var jsonString = links[i].getAttribute(\"data-store\");" +
            "var obj = JSON && JSON.parse(jsonString) || $.parseJSON(jsonString);" +
            "var videoName = obj.videoID;" +
            "var videoLink = links[i].getAttribute(\"href\");" +
            "var res = videoLink.split(\"src=\");" +
            "var myLink = res[1];" +
            "links[i].parentNode.setAttribute('onclick', \"browser.getVideoData('\"+myLink+\"')\");" +
            "while (links[i].firstChild){" +
            "links[i].parentNode.insertBefore(links[i].firstChild," +
            "links[i]);}" +
            "links[i].parentNode.removeChild(links[i]);}}}catch(e){}}" +
            "getVideoLink();";

    public static String INSTAGRAM_SCRIPT = "javascript:function clickOnVideo() {var videoLink;" +
            "try{ar items = document.getElementsByTagName(\"video\");" +
            "for (i = 0; i < items.length; i++) {" +
            "videoLink = items[i].getAttribute(\"src\");" +
            "var classValueName = items[i].getAttribute(\"class\");}" +
            "var links = document.getElementsByTagName(\"a\");" +
            "for (i = 0; i < links.length; i++) {" +
            "f (links[i].getAttribute(\"role\") == \"button\"){" +
            "console.log('links[i].getAttribute: '+i);" +
            "links[i].parentNode.setAttribute('onclick', \"browser.getVideoData('\"+videoLink+\"')\");}}" +
            "}catch(e){}}clickOnVideo();";

    public static String TWITTER_SCRIPT = "javascript:function clickOnVideo() {" +
            "try{var items = document.getElementsByTagName(\"source\");" +
            "for (i = 0; i < items.length; i++) {" +
            "if (items[ i ].getAttribute(\"type\") == \"video/mp4\"){" +
            "var videoLink = items[i].getAttribute(\"src\");" +
            "browser.getVideoData(videoLink);}}}catch(e){}}clickOnVideo();";
}
