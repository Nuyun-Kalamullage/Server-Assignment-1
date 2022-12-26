# Server-Assignment-1 Stock Auctions with Socket programming Project

    completed date: 21-07-2021


<img  alt="Coding"  src="https://github.com/Nuyun-Kalamullage/Server-Assignment-1/blob/Public/stock%20server.png">


Our Project Includes, 

    *	5 java Files ( Main Class is in Main.java file)
    *	1 stocks.csv file 
    *	1 README.txt file 

This Project provides the following functions :

       *Display the current highest bid of stocks
       *Accept bids
       *Close bid
       *Publish updated profits
       *Subscribe to get updates on profits
       *Subscribe to get updates on bid changes
       
ex :-
If you are using windows you have to run the server and type "telnet localhost 2021" for the Client-Server side and type "telnet localhost 2022" for the Publisher-Subscriber side. Note that you have to install telnet command in windows.
Further, If you have a unix system you have to type Commands with replacing "telnet" using "nc".


Once the server is established, you should be able to connect to the server using a common communication tool such as nc or telnet.

How to Compile this Program,

    1.	First Open CMD and Type command as below,

      javac Main.java

    2.	In order to Start the Stock Auction Server enter following command in terminal

      java Main 60 (bidding period argument)
  
    3.	Eventually Auction Server will starts and Running Until Remaining time (can adjust bidding period time in minutes when enter it as argument on command line as above )



client-server
=============

    Client needs to connect to the server Using Telnet Using Following Command
    Telnet [ip address] 2021

If Server Locates Remotely [ip address] would be public ip address of pc

Otherwise Server Locates Locally [ip address] would be localhost

Whenever Clients Connects to Auction Server welcome screen pop-ups and ask for Unique user id otherwise it print error message.

After Client Verified their user id they can Enter Symbol and Server displays current price of item.

    Then Client could bid on particular item using following command
    AAL 2

If client realize they need to change item to bidding they can simply type “no" and further process to bid.

[Name ID],Do you want to Bid on this item ? (Type with "yes" or "no"): no

Then enter 'confirm' for confirm bidding and Type ‘quit' for quit from server



Publisher-Subscriber
====================

    Clients needs to connect to the server Using Telnet Using Following Command
    Telnet [ip address] 2022

If Server Locates Remotely [ip address] would be public ip address of pc

Otherwise Server Locates Locally [ip address] would be localhost

Whenever Client Server welcome screen pop-ups and ask for Unique user id otherwise it print error message.

Whenever Client verify user id they could be either a Publisher or Subscriber


Publisher :

For Publish a monthly profit for related security item client must use following query format

    AAL 74904 1500
         (profit)


Subscriber :


In order to get updates about preferred security item(s) Client must be subscribe security item(s){topics} that He/She preferred using below Query format 

    PRFT AAL  FB
     [sym1][sym2]


If Subscriber want to get Bidding updates about preferred security item(s) then He/She preferred using below Query format

    BID AAL  AAPL
       [sym1][sym2]


Thank You
=============================================
