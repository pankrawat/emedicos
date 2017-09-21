package com.emedicoz.app.Response;

import com.emedicoz.app.Model.PostFile;
import com.emedicoz.app.Response.ParentResponse.PostParentResponse;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Cbc-03 on 05/31/17.
 */

public class PostResponse extends PostParentResponse implements Serializable {
    public PostData getPost_data() {
        return post_data;
    }

    public void setPost_data(PostData post_data) {
        this.post_data = post_data;
    }

    public PostData post_data;

    public class PostData implements Serializable{

        private String post_text_type;
        private String text;

        private ArrayList<PostFile> post_file;

        public String getPost_text_type() {
            return post_text_type;
        }

        public void setPost_text_type(String post_text_type) {
            this.post_text_type = post_text_type;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public ArrayList<PostFile> getPost_file() {
            return post_file;
        }

        public void setPost_file(ArrayList<PostFile> post_file) {
            this.post_file = post_file;
        }

        private String id;

        private String answer_one;

        private String right_answer;

        private String answer_four;

        private String status;

        private String answer_two;

        private String question;

        private String post_id;

        private String answer_five;

        private String answer_three;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getAnswer_two() {
            return answer_two;
        }

        public void setAnswer_two(String answer_two) {
            this.answer_two = answer_two;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getPost_id() {
            return post_id;
        }

        public void setPost_id(String post_id) {
            this.post_id = post_id;
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

    public PostData createLiveStreamData(){
        PostData ps=new PostData();


        return ps;
    }

}
