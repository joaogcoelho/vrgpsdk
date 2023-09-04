package br.com.vrsoft.vrgpsdk;

import com.gprinter.io.EthernetPort;
import com.gprinter.io.PortManager;

import java.util.Vector;

public class Printer {
    private int id;
    private PortManager mPort;
    private String ip;
    private int port;
    private boolean isOpenPort = false;

    public Printer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public enum CONN_METHOD {
        WIFI("WIFI");

        private String name;

        private CONN_METHOD(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public boolean getConnState() {
        return isOpenPort;
    }

    public void openPort() {
        this.isOpenPort = false;

        mPort = new EthernetPort(ip, port);
        isOpenPort = mPort.openPort();

        if (!isOpenPort && this.mPort != null) {
            this.mPort = null;
        }
    }

    public void closePort(int id) {
        if (this.mPort != null) {
            System.out.println("id -> " + id);
            boolean isClosed = this.mPort.closePort();
            if (isClosed) {
                this.mPort = null;
                isOpenPort = false;
            }
        }
    }

    public void sendDataImmediately(final Vector<Byte> data) {
        if (this.mPort == null) {
            return;
        }
        try {
            this.mPort.writeDataImmediately(data, 0, data.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}