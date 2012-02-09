/**
 * Copyright (C) Intersect 2010.
 * 
 * This module contains Proprietary Information of Intersect,
 * and should be treated as Confidential.
 *
 * $Id: Formats.java 2 2010-09-06 01:33:48Z intersect.engineering.team@gmail.com $
 */


import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * 
 * @version $Rev: 2 $
 */
public class Formats
{
    public static final String NEWLINE = System.getProperty("line.separator");
    public static final NumberFormat WHOLE_NUMBER_FORMATTER = new DecimalFormat("###,##0");
    public static final NumberFormat DECIMAL_FORMATTER = new DecimalFormat("###,##0.000000");

}
