package com.iisigroup.colabase.webatm.toolkit;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

/**
 * 列出目錄下所有檔案, 可加副檔名過濾
 *
 * @author axl
 */
public class DirList {
    final String DESC_FILE = "/desc.txt";

    Vector vDirs = new Vector();
    Vector vFiles = new Vector();

    private String filter = "", path = "", name = "", desc = "";

    public DirList() {

    }

    /**
     * 傳入起始路徑
     */
    public DirList(String path) {
        this.path = path;
        this.traverse(path);
    }

    /**
     * 傳入起始路徑 + 副檔名過濾
     */
    public DirList(String path, String filter) {
        this.path = path;
        this.setFillter(filter);
        this.traverse(path);
    }

    public void setFillter(String filter) {
        this.filter = filter;
    }

    public void traverse(String path) {
        File pathCurr = new File(path);
        if (!pathCurr.exists())
            return;
        if (!pathCurr.isDirectory())
            return;

        // get description
        try {
            if (new File(path + DESC_FILE).exists()) {
                desc = Misc.readStringFromFile(path + DESC_FILE);
            }
        } catch (Exception e) {
        }

        // System.out.println("DirList.path=" + pathCurr.getName() + "(" + getDesc() + ")");

        File fileList[] = (filter.length() > 0) ? pathCurr.listFiles(new FileFilterExt(filter)) : pathCurr.listFiles();
        // Count File 先不要寫, 太難了 ... (要往回算 ??)
        // int cntFileOnly = pathCurr.listFiles(new FileFilterDirList()).length;
        Arrays.sort(fileList, new ComparatorDirList()); // 先這樣再慢慢改進 ...

        try {
            for (int i = 0, cntF = 0, cntD = 0; i < fileList.length; i++) {
                if (fileList[i].isFile()) {
                    vFiles.add(fileList[i].getName());
                } else if (fileList[i].isDirectory()) {
                    vDirs.add(new DirList(fileList[i].getCanonicalPath(), filter));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getDirCount() {
        return vDirs.size();
    }

    public int getFileCount() {
        return vFiles.size();
    }

    public Vector getDirs() {
        return vDirs;
    }

    public Vector getFiles() {
        return vFiles;
    }

    public String getDesc() {
        return desc;
    }

    public Vector getAllFiles() {
        Vector vAllFiles = new Vector();
        for (int i = 0; i < vDirs.size(); i++) {
            Vector filesOneDir = ((DirList) vDirs.get(i)).getAllFiles();
            if (filesOneDir != null) {
                for (int j = 0; j < filesOneDir.size(); j++) {
                    vAllFiles.add(filesOneDir.get(j));
                }
            }
        }

        for (int i = 0; i < vFiles.size(); i++) {
            vAllFiles.add(path + "/" + (String) vFiles.get(i));
        }
        return vAllFiles;
    }

    public int getCount(String str) {
        return 10;
    }

    public int getString(String str) {
        return 10;
    }

    /**
     * 提供給篩選檔案計算個數用's subClass ---- File.listFiles(FileFilter filter);
     */
    class FileFilterDirList implements FileFilter {
        public boolean accept(File pathname) {
            return pathname.isFile();
        }
    }

    /**
     * 提供給篩選檔案計算個數用's subClass ---- File.listFiles(FileFilter filter);
     */
    class FileFilterExt implements FileFilter {
        private String ext;

        public FileFilterExt(String ext) {
            this.ext = ext;
        }

        public boolean accept(File pathname) {
            // 是 File 才要 Filter, 好嗎 ?
            return (pathname.isFile()) ? pathname.getName().endsWith(ext) : true;
        }
    }

    /**
     * 提供給目錄排序用's subClass ---- Arrays.sort(Object[] a, Comparator c);
     */
    class ComparatorDirList implements Comparator {
        public int compare(Object o1, Object o2) {
            File f1 = (File) o1;
            File f2 = (File) o2;

            // 看看是不是目錄先? 目錄要排在檔案前面 ...
            int score1 = f1.isDirectory() ? 0 : 1;
            int score2 = f2.isDirectory() ? 0 : 1;
            if (score1 != score2) {
                return (score1 - score2);
            }

            // 剩下就是比字串囉, 不看大小寫 ...
            return f1.getName().compareToIgnoreCase(f2.getName());
        }
    }
}
