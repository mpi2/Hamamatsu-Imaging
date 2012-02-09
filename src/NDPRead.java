/**
 * Copyright (C) Intersect 2010.
 * 
 * This module contains Proprietary Information of Intersect,
 * and should be treated as Confidential.
 *
 * $Id: NDPRead.java 2 2010-09-06 01:33:48Z intersect.engineering.team@gmail.com $
 */


import java.nio.ByteBuffer;

import com.sun.jna.NativeLong;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.win32.StdCallLibrary;

/**
 * JNA interface class for the NDPRead.dll module. Below is from the supplied documentation:
 * 
 * Access to the library is via NDPRead.dll, this is supplied in both Win32 (x86) and Win64 (x64) versions and requires
 * a Microsoft Windows based environment to run (other operating systems/architectures are not currently supported). It
 * is supplied as a zip file containing the redistributable binary modules and non-redistributable header/library files.
 * The redistributable modules can be included directly in your end-user setup. The library requires GDI+ (only for
 * Windows 2000 machines or earlier since GDI+ is included as standard from WinXP onwards) the redistributable from
 * Microsoft is included in the zip file.
 * 
 * A header file is included containing the function prototypes of the library, there is also a library file (.lib) for
 * use with Microsoft Visual Studio alternatively the library can be imported using the standard
 * 'LoadLibrary'/GetProcAddress methods, the default calling convention used is cdecl but stdcall version are also
 * available for environments that don't support cdecl (e.g. Microsoft Visual Basic 6), see the next section for
 * details.
 * 
 * Images are loaded as and when required, there is no need to 'initialise' an image before using it as this is all
 * taken care of internally. The last 100 loaded images are kept 'initialised' inside the library so if your application
 * needs to keep swapping between various images this should not degrade performance in any way.
 * 
 * Parameters that begin i_ are input parameters, those that start o_ will contain output values after the call is
 * completed and those that start io_ initially contain input parameters that are changed to output values during the
 * call.
 * 
 * All coordinate and dimension parameters taken or returned by the library are physical measurements in nanometres
 * (this gives a high enough precision to allow integer based coordinates without relevant rounding errors).
 * 
 * Image data (with the exception of GetImageData16) is returned in a bottom-up interleaved BGR format padded to DWORD
 * boundaries at the end of each line (basically a bottom-up DIB without the header).
 * 
 * All functions that retrieve image data accept a user specified buffer to receive the data. Often the size of the
 * buffer may not be known before the call is made, in these cases specify a NULL buffer with a buffer size of zero and
 * the library will replace the buffer size with the required size without attempting to copy any data.
 * 
 * The library is thread-safe so you may safely make concurrent calls into the library from multiple threads if
 * required.
 * 
 * @version $Rev: 2 $
 */
public interface NDPRead extends StdCallLibrary
{
    /**
     * Returns the physical width of the specified image in nm.
     * 
     * @param i_strImageID
     *            Filename of the image to use
     * @return Width of the image in nm if the call succeeds, zero if the call fails (use GetLastErrorMessage for more
     *         details on failure).
     */
    int GetImageWidth(String i_strImageID);

    /**
     * Returns the physical height of the specified image in nm.
     * 
     * @param i_strImageID
     *            Filename of the image to use
     * @return Height of the image in nm if the call succeeds, zero if the call fails (use GetLastErrorMessage for more
     *         details on failure).
     */
    int GetImageHeight(String i_strImageID);

    /**
     * Returns the bit depth of the source image data (i.e. that which will be returned by the
     * GetImageDataInSourceBitDepth call). The GetImageData, GetMap & GetSlideImage calls always return data in 24 bit
     * BGR.
     * 
     * @param i_strImageID
     *            Filename of the image to use
     * @return Bit depth of the source image (currently 8, 12 or 24).
     */
    int GetImageBitDepth(String i_strImageID);

    /**
     * Returns the number of channels in the source image (1 for greyscale 3 for RGB) and therefore the number of
     * channels that will be present in data returned from the GetImageDataInSourceBitDepth call.
     * 
     * @param i_strImageID
     *            Filename of the image to use
     * @return No. of channels in the source image (currently 1 or 3)
     */
    int GetNoChannels(String i_strImageID);

    /**
     * Returns a constant indicating the channel order of the source image and thus the order in which multichannel data
     * will be returned from the GetImageDataInSourceBitDepth call. 0 = Undefined (error) 1 = BGR 2 = RGB 3 = Y
     * (greyscale)
     * 
     * @param i_strImageID
     *            Filename of the image to use
     * @return Channel order identifier (currently 0, 1, 2 or 3)
     */
    int GetChannelOrder(String i_strImageID);

    /**
     * Set the desired size of images returned by future GetImageData/GetMap/GetImageData16 calls.
     * 
     * @param i_nWidth
     *            - Desired frame width
     * @param i_nHeight
     *            - Desired frame height
     * @return non-zero if the call succeeds, zero if the call fails (use GetLastErrorMessage for more details on
     *         failure).
     */
    int SetCameraResolution(NativeLong i_nWidth, NativeLong i_nHeight);

    /**
     * Gets an overview image of the whole scan that fits into the frame size specified by SetFrameSize. Note that
     * unless the aspect ratio of the scanned image exactly matches the aspect ratio of the requested frame size then
     * this returned image will be smaller than the requested frame size in either the x or y axis.
     * 
     * @param i_strImageID
     *            Filename of the image to use
     * @param o_nPhysicalX
     *            Physical X position of the centre of this image in nm
     * @param o_nPhysicalY
     *            Physical Y position of the centre of this image in nm
     * @param o_nPhysicalWidth
     *            Physical width of this image in nm
     * @param o_nPhysicalHeight
     *            Physical height of this image in nm
     * @param i_pBuffer
     *            User supplied buffer to receive the image data
     * @param io_nBufferSize
     *            Size of the user supplied buffer (if the supplied buffer is too small then this is updated to reflect
     *            the required size and no data is copied
     * @param o_nPixelWidth
     *            The actual width of this image in pixels
     * @param o_nPixelHeight
     *            The actual height of this image in pixels
     * @return Non-zero if the call succeeds, zero if the call fails (use GetLastErrorMessage for more details on
     *         failure).
     */
    int GetMap(String i_strImageID, NativeLongByReference o_nPhysicalX, NativeLongByReference o_nPhysicalY,
            NativeLongByReference o_nPhysicalWidth, NativeLongByReference o_nPhysicalHeight, ByteBuffer i_pBuffer,
            NativeLongByReference io_nBufferSize, NativeLongByReference o_nPixelWidth,
            NativeLongByReference o_nPixelHeight);

    /**
     * Gets the image of the entire glass slide that was scanned (if available). Note that the image is always returned
     * in the source resolution and doesn't take account of the user supplied frame width/height. To obtain the required
     * buffer size call first with a buffer size of zero.
     * 
     * @param i_strImageID
     *            Filename of the image to use
     * @param o_nPhysicalX
     *            Physical X position of the centre of this image in nm
     * @param o_nPhysicalY
     *            Physical Y position of the centre of this image in nm
     * @param o_nPhysicalWidth
     *            Physical width of this image in nm
     * @param o_nPhysicalHeight
     *            Physical height of this image in nm
     * @param i_pBuffer
     *            User supplied buffer to receive the image data
     * @param io_nBufferSize
     *            Size of the user supplied buffer (if the supplied buffer is too small then this is updated to reflect
     *            the required size and no data is copied
     * @param o_nPixelWidth
     *            The width of this image in pixels
     * @param o_nPixelHeight
     *            The height of this image in pixels
     * @return Non-zero if the call succeeds, zero if the call fails (use GetLastErrorMessage for more details on
     *         failure).
     */
    int GetSlideImage(String i_strImageID, NativeLongByReference o_nPhysicalX, NativeLongByReference o_nPhysicalY,
            NativeLongByReference o_nPhysicalWidth, NativeLongByReference o_nPhysicalHeight, ByteBuffer i_pBuffer,
            NativeLongByReference io_nBufferSize, NativeLongByReference o_nPixelWidth,
            NativeLongByReference o_nPixelHeight);

    /**
     * All the image formats supported by NDP.read can contain multiple focal planes, this function returns the focusing
     * range available for a given image.
     * 
     * @param i_strImageID
     *            Filename of the image to use
     * @param o_nMin
     *            The Z offset of the minimum focal plane (nm)
     * @param o_nMax
     *            The Z offset of the maximum focal plane (nm)
     * @param o_nStep
     *            The distance between each available plane (nm)
     * @return Non-zero if the call succeeds, zero if the call fails (use GetLastErrorMessage for more details on
     *         failure).
     */
    int GetZRange(String i_strImageID, NativeLongByReference o_nMin, NativeLongByReference o_nMax,
            NativeLongByReference o_nStep);

    /**
     * Retrieves a region of interest from the specified image at the specified position and resolution in 24-bit BGR
     * format.
     * 
     * @param i_strImageID
     *            Filename of the image to use
     * @param i_nPhysicalXPos
     *            Physical X pos of the centre of the desired image in nm
     * @param i_nPhysicalYPos
     *            Physical Y pos of the centre of the desired image in nm
     * @param i_nPhysicalZPos
     *            Physical Z (focal) pos of the of the desired image in nm
     * @param i_fMag
     *            Desired equivalent objective magnification of the image.
     * @param o_nPhysicalWidth
     *            Physical width of this image in nm
     * @param o_nPhysicalHeight
     *            Physical height of this image in nm
     * @param i_pBuffer
     *            User supplied buffer to receive the image data
     * @param io_nBufferSize
     *            Size of the user supplied buffer (if the supplied buffer is too small then this is updated to reflect
     *            the required size and no data is copied
     * @return Non-zero if the call succeeds, zero if the call fails (use GetLastErrorMessage for more details on
     *         failure).
     */
    int GetImageData(String i_strImageID, NativeLong i_nPhysicalXPos, NativeLong i_nPhysicalYPos,
            NativeLong i_nPhysicalZPos, float i_fMag, NativeLongByReference o_nPhysicalWidth,
            NativeLongByReference o_nPhysicalHeight, ByteBuffer i_pBuffer, NativeLongByReference io_nBufferSize);

    /**
     * Returns the objective lens that the specified image was captured at.
     * 
     * @param i_strImageID
     *            Filename of the image to use.
     * @return Lens magnification that the image was captured at if the call succeeds, zero if the call fails (use
     *         GetLastErrorMessage for more details on failure).
     */
    float GetSourceLens(String i_strImageID);

    /**
     * Returns the size of the specified image in pixels.
     * 
     * @param i_strImageID
     *            Filename of the image to use
     * @param o_nWidth
     *            Width of this image in pixels
     * @param o_nHeight
     *            Height of this image in pixels
     * @return Non-zero if the call succeeds, zero if the call fails (use GetLastErrorMessage for more details on
     *         failure).
     */
    int GetSourcePixelSize(String i_strImageID, NativeLongByReference o_nWidth, NativeLongByReference o_nHeight);

    /**
     * Uninitialises any images stored in its internal cache, useful to free up memory or unlock files so that they can
     * be moved or deleted.
     * 
     * @return Non-zero if the call succeeds, zero if the call fails (use GetLastErrorMessage for more details on
     *         failure).
     */
    int CleanUp();

    /**
     * Returns an error message describing the cause of the last failure.
     * 
     * @return String containing a textual description of the last error that occurred.
     */
    String GetLastErrorMessage();

    /**
     * Returns the textual reference associated with the slide if available (e.g. that from the barcode
     * or manually entered when the slide was scanned )
     * 
     * @param i_strImageID
     * @return Non-zero if the call succeeds, zero if the call fails (use GetLastErrorMessage for more details on
     *         failure).
     */
    int GetReference(String i_strImageID, ByteBuffer i_pBuffer, NativeLongByReference io_nBufferSize);
    
}
