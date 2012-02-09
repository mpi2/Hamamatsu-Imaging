/**
 * Copyright (C) Intersect 2010.
 * 
 * This module contains Proprietary Information of Intersect,
 * and should be treated as Confidential.
 *
 * $Id: NDPReadException.java 2 2010-09-06 01:33:48Z intersect.engineering.team@gmail.com $
 */


/**
 * General exception for any unexpected errors returned by the NDPRead JNA classes
 * 
 * @version $Rev: 2 $
 */
public class NDPReadException extends Exception
{

    private static final long serialVersionUID = 1L;
    
    private final String details;
    private final String messageFromNdpRead;

    public NDPReadException(String details, String messageFromNdpRead)
    {
        super(details + " message from NDPRead interface: [" + messageFromNdpRead + "]");
        this.details = details;
        this.messageFromNdpRead = messageFromNdpRead;
    }

    public String getDetails()
    {
        return details;
    }

    public String getMessageFromNdpRead()
    {
        return messageFromNdpRead;
    }

}
