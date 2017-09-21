package com.emedicoz.app.Model;

import java.io.Serializable;

/**
 * Created by Cbc-03 on 05/31/17.
 */

public class PostMCQ implements Serializable {

    static PostMCQ postMCQ;

    public static PostMCQ newInstance() {
        postMCQ = new PostMCQ();
        return postMCQ;
    }

    public static PostMCQ getInstance() {
        if (postMCQ == null) {
            postMCQ = new PostMCQ();
        }
        return postMCQ;
    }

    private String answer_one;

    private String right_answer;

    private String answer_four;

    private String answer_two;

    private String user_id;

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    private String post_id;

    private String question;

    private String answer_five;

    private String answer_three;

    public String getAnswer_one() {
        return answer_one;
    }

    public void setAnswer_one(String answer_one) {
        this.answer_one = answer_one;
    }

    public String getRight_answer() {
        return right_answer;
    }

    public void setRight_answer(String right_answer) {
        this.right_answer = right_answer;
    }

    public String getAnswer_four() {
        return answer_four;
    }

    public void setAnswer_four(String answer_four) {
        this.answer_four = answer_four;
    }

    public String getAnswer_two() {
        return answer_two;
    }

    public void setAnswer_two(String answer_two) {
        this.answer_two = answer_two;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer_five() {
        return answer_five;
    }

    public void setAnswer_five(String answer_five) {
        this.answer_five = answer_five;
    }

    public String getAnswer_three() {
        return answer_three;
    }

    public void setAnswer_three(String answer_three) {
        this.answer_three = answer_three;
    }

}
