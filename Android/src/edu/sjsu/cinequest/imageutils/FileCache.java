package edu.sjsu.cinequest.imageutils;

import java.io.File;
import android.content.Context;

/*
    Implements a simple file cache
*/
public class FileCache {
    
    private File cacheDir;
    
    /**
     *  Constructs a new cache object and directory
     *  @param context The android context we are running the application on.
     */
    public FileCache(Context context){
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"TTImages_cache");
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }
    
    /**
     *  Creates a File object from the cache directory
     *  and identifies it with a hashcode
     *  @param url A path to a file
     *  @return a new File from the cache
     */
    public File getFile(String url){
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        String filename=String.valueOf(url.hashCode());
        //Another possible solution (thanks to grantland)
        //String filename = URLEncoder.encode(url);
        File f = new File(cacheDir, filename);
        return f;
        
    }
    
    /**
     *  Removes all files in the cache if there is any
     */
    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }

}