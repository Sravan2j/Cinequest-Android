/*
    Copyright 2008 San Jose State University
    
    This file is part of the Blackberry Cinequest client.

    The Blackberry Cinequest client is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    The Blackberry Cinequest client is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the Blackberry Cinequest client.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.sjsu.cinequest.comm;

import java.util.Hashtable;

/**
 * Utility class to for replacing special characters and editing string buffers
 * @author Cay Horstmann
 */
public class CharUtils
{
    private static Hashtable replacements = new Hashtable();
    static
    {
        replacements.put("amp", new Character('&'));
        replacements.put("quot", new Character('"'));
        replacements.put("lt", new Character('<'));
        replacements.put("gt", new Character('>'));
        replacements.put("reg", new Character('\u00ae'));
        replacements.put("ndash", new Character('\u2013'));
        replacements.put("mdash", new Character('\u2014'));
        replacements.put("lsquo", new Character('\u2018'));
        replacements.put("rsquo", new Character('\u2019'));
        replacements.put("ldquo", new Character('\u201c'));
        replacements.put("rdquo", new Character('\u201d'));
        replacements.put("bull", new Character('\u2022'));
        
        replacements.put("auml", new Character('\u00e4'));
        replacements.put("uuml", new Character('\u00fc'));
        replacements.put("ecirc", new Character('\u00ea'));        
    }

    /**
     * Replaces HTML entities in a string buffer
     * @param buffer the string buffer
     */
    public static void replaceEntities(StringBuffer buffer)
    {
        int i = 0;
        while (i < buffer.length())
        {
            if (buffer.charAt(i) == '&')
            {
                int j = i + 1;
                while (j < buffer.length() && buffer.charAt(j) != ';') j++;
                if (j < buffer.length())
                {
                    char[] chars = new char[j - i - 1];
                    buffer.getChars(i + 1, j, chars, 0);
                    Character repl = (Character) replacements.get(new String(chars));
                    if (repl != null)
                    {
                        buffer.delete(i, j);
                        buffer.setCharAt(i, repl.charValue());                        
                    }
                    else
                        i = j;
                }
                else
                    i = j;
            }
            i++;
        }
    }
    
    private static char[] win1252_80_9f = 
        {
        '\u20AC', '\u0081', '\u201A', '\u0192', 
        '\u201E', '\u2026', '\u2020', '\u2021', 
        '\u02C6', '\u2030', '\u0160', '\u2039', 
        '\u0152', '\u008D', '\u017D', '\u008F',
        '\u0090', '\u2018', '\u2019', '\u201C', 
        '\u201D', '\u2022', '\u2013', '\u2014', 
        '\u02DC', '\u2122', '\u0161', '\u203A', 
        '\u0153', '\u009D', '\u017E', '\u0178'
        };
    
    /**
     * Replaces Windows 1252 characters with their Unicode equivalents, and replaces HTML entities
     * @param str a string
     * @return the string with the replacements carried out
     */
    public static String fixWin1252AndEntities(String str)
    {
        if (str == null) return null;
        StringBuffer buffer = new StringBuffer(str);
        fixWin1252(buffer);
        CharUtils.replaceEntities(buffer);
        return buffer.toString();
    }
    
    /**
     * Replaces Windows 1252 characters with their Unicode equivalents.
     * @param buffer the buffer to be edited 
     */
    public static void fixWin1252(StringBuffer buffer) 
    {
        for (int i = 0; i < buffer.length(); i++)
        {
            char ch = buffer.charAt(i);
            if ('\u0080' <= ch && ch <= '\u009F')
                buffer.setCharAt(i, win1252_80_9f[ch - '\u0080']);            
        }
    }
    
    private static boolean isSpace(char ch)
    {
        return ch == ' ' || ch == '\n' || ch == '\t' || ch == '\r';
    }
    
    /**
     * Trims leading and trailing spaces from a string buffer
     * @param buffer the buffer to be edited 
     */
    public static void trim(StringBuffer buffer)
    {
        int i = 0;
        while (i < buffer.length() && isSpace(buffer.charAt(i))) i++;
        buffer.delete(0, i);
        int j = buffer.length() - 1;
        while (j >= 0 && isSpace(buffer.charAt(j))) j--;
        buffer.setLength(j + 1);
    }
}