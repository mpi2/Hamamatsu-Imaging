/*
 * Copyright (c) 2011 Genome Research Ltd.
 *
 * Author: Mouse Informatics Group <team110g@sanger.ac.uk>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, 
 *     this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE)ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */



import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

public class SlideImageTest  {

	private static final String JNA_LIBRARY_PATH_PROPERTY = "jna.library.path";
	
	public static void main(String[] args) {

		SlideImageTest testSlide = new SlideImageTest();

		String pathToSlide = "C:\\Users\\mng\\Desktop\\test_slide\\S000000289 - 2011-09-06 16.59.07.ndpi";
		
		try {
			testSlide.createImages(pathToSlide);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	
	
	
	public void createImages(String file) throws IOException  {

		
		
		BufferedImage image = null;
		
		// get the dll and wrap it.
		configureEnvironment();
		NDPReadWrapper wrapper = new NDPReadWrapperImpl(NDPReadFactory.INSTANCE);

		try {
			ImageInformation slideInfo = wrapper.getImageInformation(file);
			int width  = (int)slideInfo.getImageWidthInPixels();
			int height = (int)slideInfo.getImageHeightInPixels();
			
			int aspect = width / 2000;
			int nHeight = height / aspect;

			byte[] imageBytes = wrapper.getBoundedImage(file,
					0, // x
					0, // y
					1, // image centre
					1.0f, // zoom factor
					2000, // width
					nHeight // height
					);
			if(imageBytes.length > 0) {
				image = createImageFromNdpiBytes(imageBytes,2000,nHeight);
			}
			wrapper.cleanUp();
		} catch (NDPReadException e) {
			wrapper.cleanUp();
			throw new IOException("Failed to read image data from file.");
		}

		if(image != null) {
		
			// Full image size. 100% quality
			writeProcessedImage(image, "test_full.jpg", image.getWidth(),100);
	
			// Width is 800 at 80% quality, height is scaled correctly.
			writeProcessedImage(image, "test_tn_large.jpg",800,80);
	
			// Width is 200 at 65% quality,  height is scaled correctly. Image is centred in the 200x200 image.
			writeProcessedImage(image, "test_tn_small.jpg",200,65);
		
		}
		
	}

	
	private void writeProcessedImage(BufferedImage picture, String fileName, int scaleW, int percentQuality) throws FileNotFoundException, IOException {

		
		// scale height to the ratio of the original images height:width
		double hwRatio = new Double(picture.getHeight())/new Double(picture.getWidth());
		int scaleH = (int)Math.round(new Double(scaleW * hwRatio).intValue()); 

		BufferedImage buff = null;
		if(scaleW == 200) {	
			buff = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
		}else{
			buff = new BufferedImage(scaleW, scaleH, BufferedImage.TYPE_INT_RGB);
		}
		
		Graphics2D g = (Graphics2D)buff.getGraphics();
		
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        
        Image scaledPicture = getFasterScaledInstance(picture, scaleW, scaleH, 
        		RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
     
 
        if(scaleW == 200) {	
			int containerHalfHeight = 100;
			int imageHalfHeight = scaleH / 2;
			g.setPaint(Color.WHITE);
			g.fill(new Rectangle2D.Double(0, 0, 200, 200));
			g.drawImage(scaledPicture, 0, containerHalfHeight-imageHalfHeight, buff.getWidth(), scaleH, null);
        }else{
        	g.drawImage(scaledPicture, 0, 0, buff.getWidth(), buff.getHeight(), null);
        }
        
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
		ImageWriteParam iwp = writer.getDefaultWriteParam();
		iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		iwp.setCompressionQuality(percentQuality/100.0f);  
		
		FileImageOutputStream output = new FileImageOutputStream(new File(fileName));
		writer.setOutput(output);
		writer.write(null, new IIOImage(buff, null, null), iwp);
		writer.dispose();
		g.dispose();
		buff.flush();
		scaledPicture.flush();
		
	}
	

	private static void configureEnvironment()
	{
		String currentLibraryPath = System.getProperty(JNA_LIBRARY_PATH_PROPERTY);

		if (currentLibraryPath == null)
		{
			currentLibraryPath = "";
		}

		String workingDirectory = System.getProperty("user.dir");
		// add these in just in case it helps
		String extraPaths = "\\WINDOWS\\system32;\\dll";
		// add the current working directory/dll which should be correct
		String workingDirectoryPath = workingDirectory + File.separator + "dll";
		// include the current library path (if set), working directory, plus extras
		String newLibraryPath = currentLibraryPath + ";" + workingDirectoryPath + ";" + extraPaths;
	        
		System.setProperty(JNA_LIBRARY_PATH_PROPERTY, newLibraryPath);
		
	}

	private BufferedImage createImageFromNdpiBytes(byte[] imageBytes, int pixelWidth, int pixelHeight) throws IOException
	{
		int pixelStride = 3;
		int scanlineStride = calculateScanlineStride(pixelWidth, pixelStride);
		int[] bands = {2, 1, 0};

		BufferedImage image = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_3BYTE_BGR);
		DataBufferByte dataByteBuffer = new DataBufferByte(imageBytes, imageBytes.length);
		Raster raster = Raster.createInterleavedRaster(dataByteBuffer, pixelWidth, pixelHeight, scanlineStride,
				pixelStride, bands, null);
		image.setData(raster);
	
		return image;
	}

	/**
	 * Calculate the number of bytes per "row" in the image. Each pixel is [pixelStride] bytes, but lines are padded to
	 * 4 byte (DWORD) boundaries
	 * 
	 * @param pixelWidth
	 *            of the image data
	 * @param pixelStride
	 *            the number of bytes per pixel
	 * @return
	 */
	protected int calculateScanlineStride(int pixelWidth, int pixelStride)
	{
		// byte array is padded to dword boundaries (4 bytes) so calculate what the row length (scanline stride) will be
		int pixelBytesPerRow = pixelWidth * pixelStride;
		int mod = pixelBytesPerRow % 4;
		int padding = mod > 0 ? 4 - mod : 0;
		int scanlineStride = (pixelWidth * pixelStride) + padding;
		return scanlineStride;
	}


	
	
    public BufferedImage getFasterScaledInstance(BufferedImage img,
            int targetWidth, int targetHeight, Object hint,
            boolean progressiveBilinear)
    {
        int type = (img.getTransparency() == Transparency.OPAQUE) ?
            BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = img;
        BufferedImage scratchImage = null;
        Graphics2D g2 = null;
        int w, h;
        int prevW = ret.getWidth();
        int prevH = ret.getHeight();
        boolean isTranslucent = img.getTransparency() !=  Transparency.OPAQUE; 

        if (progressiveBilinear) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }
        
        do {
            if (progressiveBilinear && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (progressiveBilinear && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            if (scratchImage == null || isTranslucent) {
                // Use a single scratch buffer for all iterations
                // and then copy to the final, correctly-sized image
                // before returning
                scratchImage = new BufferedImage(w, h, type);
                g2 = scratchImage.createGraphics();
            }
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, w, h, 0, 0, prevW, prevH, null);
            prevW = w;
            prevH = h;

            ret = scratchImage;
        } while (w != targetWidth || h != targetHeight);
        
        if (g2 != null) {
            g2.dispose();
        }

        // If we used a scratch buffer that is larger than our target size,
        // create an image of the right size and copy the results into it
        if (targetWidth != ret.getWidth() || targetHeight != ret.getHeight()) {
            scratchImage = new BufferedImage(targetWidth, targetHeight, type);
            g2 = scratchImage.createGraphics();
            g2.drawImage(ret, 0, 0, null);
            g2.dispose();
            ret = scratchImage;
        }
        
        return ret;
    }
	

}
