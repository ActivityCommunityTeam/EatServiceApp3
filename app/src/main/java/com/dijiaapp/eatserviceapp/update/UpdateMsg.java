package com.dijiaapp.eatserviceapp.update;

/**
 * Created by johe on 2016/12/29.
 */

public class UpdateMsg  {
    String client_version;
    String download_url;
    String update_log;
    String update_install;

    public UpdateMsg (){

    }
    public UpdateMsg (String client_version,
            String download_url,
            String update_log,
            String update_install){
            this.client_version=client_version;
        this.download_url=download_url;
        this.update_install=update_install;
        this.update_log=update_log;
    }
    public String getClient_version() {
        return client_version;
    }

    public void setClient_version(String client_version) {
        this.client_version = client_version;
    }

    public String getUpdate_install() {
        return update_install;
    }

    public void setUpdate_install(String update_install) {
        this.update_install = update_install;
    }

    public String getUpdate_log() {
        return update_log;
    }

    public void setUpdate_log(String update_log) {
        this.update_log = update_log;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    @Override
    public String toString() {
        return "UpdateMsg{" +
                "client_version='" + client_version + '\'' +
                ", download_url='" + download_url + '\'' +
                ", update_log='" + update_log + '\'' +
                ", update_install='" + update_install + '\'' +
                '}';
    }
}
