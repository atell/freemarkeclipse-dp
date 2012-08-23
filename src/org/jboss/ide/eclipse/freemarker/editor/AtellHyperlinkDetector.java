package org.jboss.ide.eclipse.freemarker.editor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.jboss.ide.eclipse.freemarker.Plugin;

public class AtellHyperlinkDetector implements IHyperlinkDetector {
   static String[] tags=new String[]{"<#include"};
//   static {
//      try{
//      String dpTagsString = Plugin.getInstance().getResourceBundle().getString("dp_tags");
//      tags = dpTagsString.split(",");
//      }catch(RuntimeException e){
//         Plugin.log(e.getMessage());
//      }
//   }
//   static String   basePtah = Plugin.getInstance().getResourceBundle().getString("dp_freemarker_base_path");

   public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
      IDocument doc = textViewer.getDocument();// get doc
      //鼠标位置
      int currentPos = region.getOffset();
      int currentPosInLine;
      //当前行的位置
      String line;
      int lineOffset;
      try {
         IRegion lineInfo = doc.getLineInformationOfOffset(currentPos);
         lineOffset = lineInfo.getOffset();
         line = doc.get(lineOffset, lineInfo.getLength());
         currentPosInLine = currentPos - lineInfo.getOffset();
      } catch (BadLocationException localBadLocationException) {
         return null;
      }
      //当前行是否包含“<#include 或 <#import ”
      //如有，找出“<#include 或 <#import ”之后的双引号内的content
      //如果当前位置currentPos在content范围内，则content显示连接，path为...
      String content = null;
      int leftDoubleQuote = 0, rightDoubleQuote = 0;
      for (String tag : tags) {
         int index = line.indexOf(tag);
         if (index != -1) {
            //找出“<#include 或 <#import ”之后的双引号内的content
            int i = index + tag.length();
            while (i < line.length() && line.charAt(i) != '\"') {
               i++;
            }
            if (i < line.length()) {
               leftDoubleQuote = i;
               i++;
               while (i < line.length() && line.charAt(i) != '\"') {
                  i++;
               }
               if (i < line.length()) {
                  rightDoubleQuote = i;
                  content = line.substring(leftDoubleQuote + 1, rightDoubleQuote);
               }
            }
         }
         //找到content，如果当前位置currentPos在content范围内，则content为最终结果，否则继续下一个tag
         if (content != null && content.length() > 0) {
            //当前位置currentPos在content范围内，则content为最终结果
            if (leftDoubleQuote <= currentPosInLine && currentPosInLine <= rightDoubleQuote) {
               break;
            }
            //content不在鼠標範圍內，不是最終結果，則继续下一个tag
            content = null;
         }
      }
      //存在content，則region为[left,right], path为...
      if (content != null && content.length() > 0) {
         Region contentRegion = new Region(lineOffset+leftDoubleQuote+1, content.length());
         return new IHyperlink[] { new AtellHyperlink(contentRegion, content, textViewer) };
      }

      //               String pathStr = doc.get(leftDoubleQuotation + 1, rightDoubleQuotation - leftDoubleQuotation - 1);
      //               path = pathStr.replace(',', '/');
      //               if (path.startsWith("search/upload/")) {
      //                  path = "/templates/" + path.substring(7);
      //               } else {
      //                  path = "/templates/search/controls/" + path;
      //               }
         return null;

   }
}
