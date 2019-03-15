# Content Delivery Network (Java) [![Build Status](https://travis-ci.org/razr22/ContentDeliveryNetwork.svg?branch=master)](https://travis-ci.org/razr22/ContentDeliveryNetwork)

A simple Java implementation of a Content Delivery Network. Includes a client portal, local DNS server, two authoritative DNS servers, and a host content server. This program mimics the functionality of everyday media distribution platform CDN's such as YouTube, Facebook, and other non-media related companies. 

USAGE: 
  Import as a Java project into Eclipse or the IDE of choice.
  Make sure JRE/JDK are up to date.
  
  In 'public/index.html' modify file to include link(s) to media files.
  Create folder under 'testProj/' to serve content and name it "CDN", place media files in folder.
  Create folder under 'testProj/' that stores retrieved content and name it "retrieve".
  
  Run in order:
    1) src/CDN/his/WebServer.java
    2) src/CDN/client/LocalDNS.java
    3) src/CDN/his/AuthDNSHis.java
    4) src/CDN/hers/AuthDNSHers.java
    5) src/CDN/hers/ContentServer.java
    6) src/CDN/client/ClientApplication.java
