package app.utils;

import sun.rmi.runtime.Log;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {
    private static final int TNWIDTH = 256;
    private static final int TNHEIGT = 256;
    public static void makeThumbNail(File full, String path, String name) throws IOException {
        Logger.log("Начинаем создание миниатюры");
        BufferedImage img = ImageIO.read(full);
        int width = img.getWidth();
        int height = img.getHeight();
        int x,y,w,h;
        if (width>height){
            w = h = height;
            x = (width-w)/2;
            y = 0;
        }
        else{
            w = h =width;
            x=0;
            y=(height-h)/2;
        }
        BufferedImage subimg = img.getSubimage(x,y,w,h);
        Logger.log("Получен квадрат миниатюры");

        BufferedImage resized = new BufferedImage(TNWIDTH,TNHEIGT,subimg.getType());
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(subimg,0,0,TNWIDTH,TNHEIGT,0,0,w,h,null);
        g.dispose();
        Logger.log("Миниатюра сжаа до нужного размера");

        String outpath = path+"/" + name + "-tn.png";
        ImageIO.write(resized,"png",new File(outpath));
        Logger.log("Миниатюра сохранена");
    }
}
