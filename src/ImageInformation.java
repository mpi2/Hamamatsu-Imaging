/**
 * Copyright (C) Intersect 2010.
 * 
 * This module contains Proprietary Information of Intersect,
 * and should be treated as Confidential.
 *
 * $Id: ImageInformation.java 2 2010-09-06 01:33:48Z intersect.engineering.team@gmail.com $
 */




/**
 * Wrapper class that holds various pieces of info about an NDPI file.
 * 
 * @version $Rev: 2 $
 */
public class ImageInformation
{

   
    private long imageWidthInPixels;
    private long imageHeightInPixels;
    

    public long getImageWidthInPixels()
    {
        return imageWidthInPixels;
    }

    public void setImageWidthInPixels(long imageWidthInPixels)
    {
        this.imageWidthInPixels = imageWidthInPixels;
    }

    public long getImageHeightInPixels()
    {
        return imageHeightInPixels;
    }

    public void setImageHeightInPixels(long imageHeightInPixels)
    {
        this.imageHeightInPixels = imageHeightInPixels;
    }


}
