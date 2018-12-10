package com.selme.presenter.service;

public class PostService {

    public int calcValue(int pickPic, int amountPickPic) {
        if (amountPickPic == 0) return 0;
        else return (pickPic * 100) / amountPickPic;
    }

}
