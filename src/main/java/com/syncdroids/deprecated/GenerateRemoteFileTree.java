package com.syncdroids.deprecated;

@Deprecated
public class GenerateRemoteFileTree {



    /*
    public static void parseRemoteDirectory(FTPClient ftpSession, String ftpPath) throws IOException {
        // If ftpPath is null, set to root
        if (ftpPath == null || ftpPath.isEmpty()) {
            ftpPath = "/";
        }

        if (ftpSession == null || !ftpSession.isConnected()){

        }

        FTPFile[] ftpFiles = ftpSession.listFiles(ftpPath);
        ArrayList<File> files = new ArrayList<>(0);

        for (FTPFile file : ftpFiles) {
            if (ftpSession.sendNoOp()) {
                if (file.getName() != null || !file.getName().isEmpty()) {
                    InputStream iStream = ftpSession.retrieveFileStream(file.getName());
                    File file_ = File.createTempFile("temp", null);
                    FileUtils.copyInputStreamToFile(iStream, file_);
                    files.add(file_);
                    ftpSession.completePendingCommand();
                    iStream.close();
                }
            }
        }

        }

        */


}
