/**
 * Copyright (C) Intersect 2010.
 * 
 * This module contains Proprietary Information of Intersect,
 * and should be treated as Confidential.
 *
 * $Id: NDPReadFactory.java 2 2010-09-06 01:33:48Z intersect.engineering.team@gmail.com $
 */


import com.sun.jna.Native;

/**
 * @version $Rev: 2 $
 * 
 */
public class NDPReadFactory
{
    public static final NDPRead INSTANCE = (NDPRead) Native.loadLibrary("NDPRead", NDPRead.class);
}
