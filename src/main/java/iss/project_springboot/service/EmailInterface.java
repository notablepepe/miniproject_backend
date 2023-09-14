package iss.project_springboot.service;

import iss.project_springboot.model.EmailDetails;


public interface EmailInterface {

	void sendSimpleMail(EmailDetails details);

}

