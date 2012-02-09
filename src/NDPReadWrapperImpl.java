/**
 * Copyright (C) Intersect 2010.
 * 
 * This module contains Proprietary Information of Intersect,
 * and should be treated as Confidential.
 *
 * $Id: NDPReadWrapperImpl.java 2 2010-09-06 01:33:48Z intersect.engineering.team@gmail.com $
 */


import java.io.FileNotFoundException;
import java.nio.ByteBuffer;

import com.sun.jna.NativeLong;
import com.sun.jna.ptr.NativeLongByReference;


/**
 * Wraps the NDPRead JNA interface to provide a more user-friendly API. So far only the methods we actually need are
 * implemented. Further wrapper methods can be added if needed.
 * 
 * @version $Rev: 2 $
 */
public class NDPReadWrapperImpl implements NDPReadWrapper
{
  
    private static final long ERROR_RESPONSE_CODE = 0;
    private NDPRead ndpRead;
   

    public NDPReadWrapperImpl(NDPRead ndpRead)
    {
        super();
        this.ndpRead = ndpRead;
    }

    @Override
    public ImageInformation getImageInformation(String fileName) throws FileNotFoundException, NDPReadException
    {
       
        ImageInformation imageInformation = new ImageInformation();

        setPixelWidthAndHeight(fileName, imageInformation);
       
        return imageInformation;
    }

   
    
    
    public byte[] getBoundedImage(String ndpiFileName, int xPositionOfDesiredCentreInNM,
            int yPositionOfDesiredCentreInNM, int desiredFocalPositionInNM, float desiredMagnification,
            int desiredPixelWidth, int desiredPixelHeight) throws NDPReadException, FileNotFoundException
    {
        NativeLong iPhysicalXPos = new NativeLong(xPositionOfDesiredCentreInNM);
        NativeLong iPhysicalYPos = new NativeLong(yPositionOfDesiredCentreInNM);
        NativeLong iPhysicalZPos = new NativeLong(desiredFocalPositionInNM);
        NativeLongByReference width = new NativeLongByReference(new NativeLong(desiredPixelWidth));
        NativeLongByReference height = new NativeLongByReference(new NativeLong(desiredPixelHeight));
        NativeLongByReference oPhysicalWidth = new NativeLongByReference();
        NativeLongByReference oPhysicalHeight = new NativeLongByReference();
        ByteBuffer iBuffer = ByteBuffer.allocate(0);
        NativeLongByReference ioBufferSize = new NativeLongByReference(new NativeLong(0));

        // set camera resolution
        ndpRead.SetCameraResolution(new NativeLong(desiredPixelWidth), new NativeLong(desiredPixelHeight));

       
        // initial call to work out necessary buffer size
        int bufferSize = determineRequiredBufferSize(ndpiFileName, desiredMagnification, iPhysicalXPos, iPhysicalYPos,
                iPhysicalZPos, oPhysicalWidth, oPhysicalHeight, iBuffer, ioBufferSize);

        // allocate buffer of the required size
        iBuffer = ByteBuffer.allocate(bufferSize);
        
        cleanUp();
        
        int resultCode = ndpRead.GetMap(ndpiFileName, width, height, new NativeLongByReference(),
                new NativeLongByReference(), iBuffer, ioBufferSize, new NativeLongByReference(),
                new NativeLongByReference());

        if (resultCode == ERROR_RESPONSE_CODE)
        {
            String message = ndpRead.GetLastErrorMessage();
            throw new NDPReadException("Failed to get image details through call to GetMap", message);
        }

        byte[] imageBytes = new byte[bufferSize];
        iBuffer.get(imageBytes);
        cleanUp();
        
        return imageBytes;
        
    }
    


    private void setPixelWidthAndHeight(String fileName, ImageInformation imageInformation) throws NDPReadException
    {
        int resultCode;
        NativeLongByReference oPixelWidth = new NativeLongByReference();
        NativeLongByReference oPixelHeight = new NativeLongByReference();
        resultCode = ndpRead.GetSourcePixelSize(fileName, oPixelWidth, oPixelHeight);
        if (resultCode == ERROR_RESPONSE_CODE)
        {
            String message = ndpRead.GetLastErrorMessage();
            throw new NDPReadException("Failed to get pixel size through call to GetSourcePixelSize", message);
        }
        imageInformation.setImageHeightInPixels(oPixelHeight.getValue().longValue());
        imageInformation.setImageWidthInPixels(oPixelWidth.getValue().longValue());
    }

    private int determineRequiredBufferSize(String ndpiFileName, float desiredMagnification, NativeLong iPhysicalXPos,
            NativeLong iPhysicalYPos, NativeLong iPhysicalZPos, NativeLongByReference oPhysicalWidth,
            NativeLongByReference oPhysicalHeight, ByteBuffer iBuffer, NativeLongByReference ioBufferSize)
        throws NDPReadException
    {
        // initial call with zero buffer size to find out the size of buffer we need
        long resultCode = ndpRead.GetImageData(ndpiFileName, iPhysicalXPos, iPhysicalYPos, iPhysicalZPos,
                desiredMagnification, oPhysicalWidth, oPhysicalHeight, iBuffer, ioBufferSize);

        if (resultCode == ERROR_RESPONSE_CODE)
        {
            String message = ndpRead.GetLastErrorMessage();
            throw new NDPReadException("Failed on initial call to GetImageData with zero sized buffer", message);
        }
        
        // second call with correctly sized buffer
        int bufferSize = ioBufferSize.getValue().intValue();
      
        return bufferSize;
    }

    @Override
    public float getSourceLensMagnification(String ndpiFile)
    {
        return ndpRead.GetSourceLens(ndpiFile);
    }

    @Override
    public void cleanUp()
    {
        try
        {
            ndpRead.CleanUp();
        }
        catch (Exception e)
        {
            // error case log
        }

    }
}
