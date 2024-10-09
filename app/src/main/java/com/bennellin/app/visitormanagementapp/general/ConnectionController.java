package com.bennellin.app.visitormanagementapp.general;

import android.nfc.Tag;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import ae.emiratesid.idcard.toolkit.CardReader;
import ae.emiratesid.idcard.toolkit.Toolkit;
import ae.emiratesid.idcard.toolkit.ToolkitException;

public class ConnectionController {

    private static CardReader cardReader = null;
    private static Toolkit toolkit = null;


    public static boolean initialize() throws ToolkitException {
        if (toolkit == null) {
            try {
                String stringConfigPath = utils.VGL_URL;
                StringBuilder configBuilder = new StringBuilder();
                configBuilder.append("\n" + "config_directory =" + utils.path);
                configBuilder.append("\n" + "log_directory =" + stringConfigPath);
                configBuilder.append("\n" + "read_publicdata_offline = true");
//                configBuilder.append("\n" + "plugin_directory_path = " + readConfigFromAssets("config_li"));

                // ICP PROD
                configBuilder.append("\n" + "vg_url =" + "https://101.53.158.186/VGPreProd/ValidationGateway");

                toolkit = new Toolkit(true, configBuilder.toString(), MyApp.Companion.getAppContext());
                CryptoUtils.setPublickey(toolkit.getDataProtectionKey().getPublicKey());
                return true;
            } catch (ToolkitException e) {
                throw e;
            }//catch()..
        }
        return true;
    }

    public static String readConfigFromAssets(String fileName) {
        String config = "";
        try {
            InputStream inputStream = MyApp.Companion.getAppContext().getAssets().open(fileName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            config = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return config;
    }

    public static Toolkit getToolkit() throws ToolkitException {
        return toolkit;
    }

    public static Toolkit getToolkitObject() throws ToolkitException {
        if (toolkit == null) {
            throw new ToolkitException("Toolkit is not initialized.");
        }
        return toolkit;
    }


    public static CardReader initConnection() throws ToolkitException {

        if (toolkit == null) {
            throw new ToolkitException(" Please initialize Toolkit first");
        }

        if (cardReader != null) {
            if (cardReader.isConnected()) {
                cardReader.disconnect();
            }
        }

        if (cardReader == null || !cardReader.isConnected()) {
            try {
                cardReader = toolkit.getReaderWithEmiratesID();


                cardReader.connect();
//                Logger.d("Connection Success full  " + cardReader.isConnected());

            } catch (ToolkitException e) {
//                Logger.e("ToolkitException::Connection failed>" + e.getMessage());
                cardReader = null;
                throw e;
            }//catch()
            catch (Exception e) {
//                Logger.e("Exception::Connection failed with handle" + e.getMessage());
                cardReader = null;
                throw e;
            }//catch()
        }//if()
        else {
//            Logger.d("connection exists " + cardReader.isConnected());
        }
        //connection is already exits return the same.
        return cardReader;
    }//initConnection()...

    public static void setNFCParams(String cardNumber, String dob, String expiryDate) throws ToolkitException {
        if (cardReader == null || !cardReader.isConnected()) {
            return;
        }
        cardReader.setNfcAuthenticationParameters(cardNumber, dob, expiryDate);

    }


    public static CardReader getConnection() throws ToolkitException {
        if (toolkit == null) {
            throw new ToolkitException("Toolkit is not initialized.");
        }
        if (cardReader == null || !cardReader.isConnected()) {
            throw new ToolkitException("Card not connected");
        }
        return cardReader;
    }//getConnection() ..

    public static CardReader initConnection(Tag tag) throws ToolkitException {
        if (toolkit == null) {
            if (toolkit == null) {
                throw new ToolkitException(" Please initialize Toolkit first");
            }
        }
        try {
            if (cardReader != null && cardReader.isConnected()) {
                closeConnection();
            }
//            Logger.d("Creating a new connection successfully initialized");
            toolkit.setNfcMode(tag);
//          discover all the readers connected to the system
            CardReader[] cardReaders = toolkit.listReaders();


            if (cardReaders == null || cardReaders.length == 0) {
//                Logger.e("No reader are founded");
                return cardReader;
            }//if()
//            Logger.d("list reader successful" + cardReaders.length);

            cardReader = new CardReader(cardReaders[0].getName());
            //Get the first reader.

//            Logger.d("list reader successful" + cardReader.getName());


            cardReader.connect();
//            Logger.d("Connection Success full  " + cardReader.isConnected());

        } catch (ToolkitException e) {
//            Logger.e("ToolkitException::Connection failed>" + e.getMessage());
            cardReader = null;
            throw e;
        }//catch()
        catch (Exception e) {
//            Logger.e("Exception::Connection failed with handle" + e.getMessage());
            cardReader = null;
            throw e;
        }//catch()
        //connection is already exits return the same.
        return cardReader;
    }//initConnection()...

    public static void closeConnection() {
//        Logger.d("Disconnecting ");
        if (null == cardReader) {
            return;
        }//if()
        try {
            if (cardReader.isConnected()) {

                cardReader.disconnect();
//                Logger.d("Reader Disconnected Status ");
            }//if()
        } catch (ToolkitException e) {
//            Logger.e("Failed to disconnect" + e.getMessage() + ",,," + e.getCode());
        }//
        finally {
//            Logger.d("connection Reset");
        }//finally
    }//closeConnection()....

    public static void cleanup() {
        if (toolkit != null) {
            try {
                closeConnection();
                toolkit.cleanup();
            } catch (ToolkitException e) {
//                Logger.e("Failed to disconnect" + e.getMessage() + ",,," + e.getCode());
            } finally {
                toolkit = null;
            }//finally
        }//if()
    }//cleanup()

    private static String readFileFromPath(String path) {

        //read the file
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }//if()

        //create stream to read the file
        FileInputStream in = null;
        String fileContents = null;
        try {
            in = new FileInputStream(file);
            byte[] contents = new byte[in.available()];
            in.read(contents);
            fileContents = new String(contents);
//            Logger.d("File read completed successfully.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
//            Logger.e("File read failed . " + e.getLocalizedMessage());
        } catch (IOException e) {
            e.printStackTrace();
//            Logger.e("File read failed . " + e.getLocalizedMessage());
        }//catch
        finally {
            if (in != null) {

                //close the stream
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }//catch()
            }//if(()
        }//finally
        return fileContents;
    }//readFileFromPath
}//end-of-class

