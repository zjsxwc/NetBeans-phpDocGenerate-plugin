/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wangchao.phpDocGenerate;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorRegistry;

/**
 *
 * @author wangchao
 */
public class PHPDocGenerate implements 
ActionListener  {

    @Override
    public void actionPerformed(ActionEvent e) {
        
        //获取当前光标位置
        JTextComponent editor = EditorRegistry.lastFocusedComponent();
        Document doc = editor.getDocument();
        int dotOffset = editor.getCaretPosition();
        
        String phpDocStart = "";
        try {
            //判断前面是否 "/**"
            phpDocStart = doc.getText(dotOffset -3, 3);
        } catch (BadLocationException ex) {
            Logger.getLogger(PHPDocGenerate.class.getName()).log(Level.SEVERE, null, ex);
        }
        if ("/**".equals(phpDocStart)) {
            //寻找下面的 $XXX<空格>或"="
            String argName = "";
            int curOffset = 1;
            String curChar = "";
            boolean startArgName = false;
            while (true) {                
                try {
                    curChar = doc.getText(dotOffset + curOffset, 1);
                } catch (BadLocationException ex) {
                    Logger.getLogger(PHPDocGenerate.class.getName()).log(Level.SEVERE, null, ex);
                    break;
                }
                if (startArgName && (curChar.equals(" ")||curChar.equals("\t")||curChar.equals("=")|| curChar.equals("\n"))) {
                    break;
                }
                if (curChar.equals("$")) {
                    startArgName = true;
                }
                if (startArgName) {
                    argName = argName + curChar;
                }
                curOffset ++;
            }
            if (!startArgName) {
                return;
            }
            argName = argName.trim();
            if (argName.length() == 0) {
                return;
            }
            
            try {
                doc.insertString(dotOffset," @var  " + argName + " */", null);
                editor.setCaretPosition(dotOffset + " @var ".length());
            } catch (BadLocationException ex) {
                Logger.getLogger(PHPDocGenerate.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            return;
        }
        
        
        
        String line = "";
        int curOffset = -1;
        String curChar = "";
        while (true) {
            if ((dotOffset + curOffset) < 0) {
                break;
            }
            try {
                curChar = doc.getText(dotOffset + curOffset, 1);
            } catch (BadLocationException ex) {
                Logger.getLogger(PHPDocGenerate.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            if ("\n".equals(curChar)) {
                break;
            }
            line = curChar + line;
            curOffset -= 1;
        }
        //line ==>         foreach ($oldSpecificationValues as $specificationValue) {
        String argListName = "";
        String perArgName = "";
        String trimedLine = line.trim();
        if (trimedLine.contains("foreach (") && trimedLine.contains(" as ") && trimedLine.contains(") {")) {
            String[] seg1List = trimedLine.split(" as ");
            if (seg1List.length == 2) {
                String[] seg2List = seg1List[0].split("foreach \\(");
                if (seg2List.length == 2) {
                    argListName = seg2List[1];
                    argListName = argListName.trim();
                    
                    String[] seg3List = seg1List[1].split("\\) \\{");
                    if (seg3List.length > 0) {
                        perArgName = seg3List[0];
                        perArgName = perArgName.trim();
                    }
                }
            }
        }
        String argTypeListName = "";
        if (argListName.length() > 0) {
            //寻找之前的 /** @var SpecificationValue[] $oldSpecificationValues */

            try {
                String text = doc.getText(0, dotOffset + curOffset);
                int posArgListName = text.lastIndexOf(argListName + " */");
                if (posArgListName > 0) {
                    String text2 = doc.getText(0, posArgListName);
                    String token = "/** @var ";
                    int posVar = text2.lastIndexOf(token);
                    if (posVar > 0) {
                        argTypeListName = text2.substring(posVar + token.length());
                        argTypeListName = argTypeListName.trim();
                    }
                }
            } catch (BadLocationException ex) {
                Logger.getLogger(PHPDocGenerate.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }
        if ((argTypeListName.length() > 0)&& (argTypeListName.contains("[]"))) {
            String perArgTypeName = argTypeListName.substring(0, argTypeListName.length() - 2);
            if (perArgTypeName.length() > 0) {
                int foreachLinePos = line.indexOf("foreach (");
                String whiteCharString = line.substring(0, foreachLinePos);
                try {
                    doc.insertString(dotOffset + curOffset + 1, whiteCharString + "/** @var " + perArgTypeName + " " + perArgName + " */\n", null);
                } catch (BadLocationException ex) {
                    Logger.getLogger(PHPDocGenerate.class.getName()).log(Level.SEVERE, null, ex);
                    return;
                }
            }
        }
        
    }
    
}
