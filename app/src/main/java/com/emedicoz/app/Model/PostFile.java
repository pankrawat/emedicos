package com.emedicoz.app.Model;

import java.io.Serializable;

/**
 * Created by Cbc-03 on 06/19/17.
 */

public class PostFile implements Serializable {

    private String id;
    private String post_id;
    private String file_type;
    private String link;
    private String file_info;

    public String getFile_info() {
        return file_info;
    }

    public void setFile_info(String file_info) {
        this.file_info = file_info;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


}
