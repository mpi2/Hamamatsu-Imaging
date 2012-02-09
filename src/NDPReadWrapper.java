/**
 * Copyright (C) Intersect 2010.
 * 
 * This module contains Proprietary Information of Intersect,
 * and should be treated as Confidential.
 *
 * $Id: NDPRead.java 2 2010-09-06 01:33:48Z intersect.engineering.team@gmail.com $
 */

import java.io.FileNotFoundException;


public interface NDPReadWrapper
{

 
    public ImageInformation getImageInformation(String fileName) throws FileNotFoundException, NDPReadException;

 
    public byte[] getBoundedImage(String ndpiFileName, int xPositionOfDesiredCentreInNM,
            int yPositionOfDesiredCentreInNM, int desiredFocalPositionInNM, float desiredMagnification,
            int desiredPixelWidth, int desiredPixelHeight) throws NDPReadException, FileNotFoundException;
    
    public float getSourceLensMagnification(String ndpiFile);

    public void cleanUp();
    
    
   

}
