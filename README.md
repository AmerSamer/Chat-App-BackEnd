# Chat-App-BackEnd
1. **Instructions to run the program:** <br />
  https://www.gmass.co/blog/gmail-smtp/ <br />
  Long story short: <br />
  open your gmail -> go to account settings -> security -> two-factor auth with phone -> go to app passwords -> add new app -> you will get a password -> copy it. <br />


2. **Add in resource folder under /src/main 2 files:** <br />
  I.mail.properties <br />
  II.application.properties <br />

  I. mail.properties: <br />
        spring.mail.host=smtp.gmail.com  <br /> 
        spring.mail.port=587  <br />
        spring.mail.username= **"Insert here your gmail"**  <br />
        spring.mail.password= **"Insert here the password you copied from 1"**   <br />
        spring.mail.properties.mail.smtp.auth=true  <br />
        spring.mail.properties.mail.smtp.starttls.enable=true  <br />
    
  II. application.properties: (Using MySQL) <br />
        spring.jpa.hibernate.ddl-auto=update  <br />
        spring.datasource.url=jdbc:mysql://localhost:**"Enter your MySQL port here"**/chatapp  <br />
        spring.datasource.username=**"Your username in MySQL"**   <br />
        spring.datasource.password=**"Your password in MySQL"**  <br />
        spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver  <br />
        server.error.include-message=always  <br />
        
3.**Activate pom.xml**
4.**Run the code and the front-end code:**
    https://github.com/AmerSamer/Chat-App-FrontEnd/blob/main/README.md
