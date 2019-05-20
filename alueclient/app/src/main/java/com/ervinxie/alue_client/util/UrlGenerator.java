package com.ervinxie.alue_client.util;

public class UrlGenerator {
    private UrlGenerator(){};


    public static String clientId = "/?client_id=747fc987bb098bd800270b4f2e684a2fc3414849d9eb3190766266167221d986";
    public static String Header = "https://api.unsplash.com";

    public static String MainCollectionId = "4795842";

    public static String CollectionInfo(){
        return Header+"/collections"+"/"+MainCollectionId+clientId;
    }

    public static String CollectionInfo(String id){
        return Header+"/collections"+"/"+id+clientId;
    }

    public static String CollectionPhotos(int page,int per_page){
        return Header+"/collections"+"/"+MainCollectionId+"/"+"photos"+clientId+"&page="+page+"&per_page="+per_page;
    }
    public static String CollectionPhotos(String id,int page,int per_page){
        return Header+"/collections"+"/"+id+"/"+"photos"+clientId+"&page="+page+"&per_page="+per_page;
    }


}
