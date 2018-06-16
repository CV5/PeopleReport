package com.peoplereport.peoplereport.ClassModels;

/**
 * creado por  Christian en la fecha 2018-06-11.
 */

public class ViewsModels {

    private String ViewsId;
    private String viewsPhotoUrl;

    public ViewsModels() {
    }

    public ViewsModels(String viewsId, String viewsPhotoUrl) {
        ViewsId = viewsId;
        this.viewsPhotoUrl = viewsPhotoUrl;
    }

    public String getViewsId() {
        return ViewsId;
    }

    public void setViewsId(String viewsId) {
        ViewsId = viewsId;
    }

    public String getViewsPhotoUrl() {
        return viewsPhotoUrl;
    }

    public void setViewsPhotoUrl(String viewsPhotoUrl) {
        this.viewsPhotoUrl = viewsPhotoUrl;
    }
}
