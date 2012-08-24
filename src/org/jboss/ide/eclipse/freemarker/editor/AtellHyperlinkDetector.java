package org.jboss.ide.eclipse.freemarker.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.jboss.ide.eclipse.freemarker.Plugin;

public class AtellHyperlinkDetector implements IHyperlinkDetector {
   static String[]  tags = new String[] { "<#include", "<#import" };

   static {
      try {
         String dpTagsString = Plugin.getInstance().getResourceBundle().getString("dp_tags");
         tags = dpTagsString.split(",");
      } catch (RuntimeException e) {
         Plugin.log(e.getMessage());
      }
   }
   private String   filePath;
   private IProject iproject;

   public AtellHyperlinkDetector(Editor editor) {
      IFile ifile = editor.getFile();
      iproject = editor.getProject();
      this.filePath = ifile.getProjectRelativePath().toOSString();
//      Plugin.log(ifile.getProjectRelativePath().toOSString());
//      Plugin.log(ifile.getFullPath().toOSString());
   }

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
         int i = 0;
         while (i < line.length()) {
            int index = line.indexOf(tag, i);
            if (index != -1) {
               //找出“<#include 或 <#import ”之后的双引号内的content
               i = index + tag.length();
               while (i < line.length() && Character.isWhitespace(line.charAt(i))) {
                  i++;
               }
               if (i < line.length() && line.charAt(i) == '\"') {
                  leftDoubleQuote = i;
                  i++;
                  while (i < line.length() && line.charAt(i) != '\"') {
                     i++;
                  }
                  if (i < line.length()) {
                     rightDoubleQuote = i;
                     content = line.substring(leftDoubleQuote + 1, rightDoubleQuote);
                     //找到content，如果当前位置currentPos在content范围内，则content为最终结果，否则继续下一个tag
                     if (content.length() > 0) {
                        //当前位置currentPos在content范围内，则content为最终结果
                        if (leftDoubleQuote <= currentPosInLine && currentPosInLine <= rightDoubleQuote) {
                           break;
                        }
                        //content不在鼠標範圍內，不是最終結果，則继续下一个tag
                        content = null;
                     }
                  }
               }
            } else {
               break;
            }
         }

      }
      //存在content，則region为[left,right], path为...
      if (content != null && content.length() > 0) {
         Region contentRegion = new Region(lineOffset + leftDoubleQuote + 1, content.length());
         return new IHyperlink[] { new AtellHyperlink(contentRegion, getPath(content), this.iproject) };
      }

      return null;

   }

   private String getPath(String content) {
      //將content最後一个/后的内容去掉
      String originContent = content;
      int index;
      while ((index = content.lastIndexOf('/')) != -1) {
         String leftPartContent = content.substring(0, index + 1);
         //判断content是否在filePath之内，如果有，则拼装成结果
         int index2 = filePath.indexOf(leftPartContent);
         if (index2 != -1) {
            String rightPartContent = originContent.substring(index + 1);
            System.out.println(leftPartContent);
            return filePath.substring(0, index2 + leftPartContent.length()) + rightPartContent;
         }
         content = leftPartContent.substring(0, leftPartContent.length()-1);//去除最后的/
      }
      return originContent;
   }
   
   private static String getPath2(String content) {
      //將content最後一个/后的内容去掉
      String filePath=  "src/main/webapp/WEB-INF/pages/index/navigation.ftl";
      String defaultPath = content;
      int index;
      while ((index = content.lastIndexOf('/')) != -1) {
         String leftPartContent = content.substring(0, index + 1);
         //判断content是否在filePath之内，如果有，则拼装成结果
         int index2 = filePath.indexOf(leftPartContent);
         if (index2 != -1) {
            String rightPartContent = defaultPath.substring(index + 1);
            System.out.println(leftPartContent);
            return filePath.substring(0, index2 + leftPartContent.length()) + rightPartContent;
         }
         content = leftPartContent.substring(0, leftPartContent.length()-1);//去除最后的/
      }
      return defaultPath;

   }
   
   public static void main(String[] args) {
      System.out.println(getPath2("pages/ss.xx"));
   }
}
