package com.ervinxie.alue_client.util;

public class UrlGenerator {
    private UrlGenerator(){};


    public static String clientId = "/?client_id=fa60305aa82e74134cabc7093ef54c8e2c370c47e73152f72371c828daedfcd7";

    public static String key = "fa60305aa82e74134cabc7093ef54c8e2c370c47e73152f72371c828daedfcd7";
    public static String Header = "https://api.unsplash.com";

    public static String MainCollectionId = "4795842";

    public static String CollectionInfo(){
        return Header+"/collections"+"/"+MainCollectionId+clientId;
    }

    public static String CollectionInfo(String id){
        return Header+"/collections"+"/"+id+clientId;
    }

    public static String CollectionPhotos(int page,int per_page){
        return Header+"/collections"+"/"+MainCollectionId+"/"+"photos"+clientId+"&per_page="+per_page+"&page="+page;
    }
    public static String CollectionPhotos(String id,int page,int per_page){
        return Header+"/collections"+"/"+id+"/"+"photos"+clientId+"&per_page="+per_page+"&page="+page;
    }


}
