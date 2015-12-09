# Don't code the mapper! 
This program is an utility that writes the java mapping code between 2 types for you. It's designed to work with JAXB annotated types.
- you do not want anymore to write if (null) then else
- you want to generate the code that maps basic fields without touching the keyboard
- you want to keep control on the code
- you dislike mappers based on configuration files, return to the basics !
- we have more interesting stuff to do !
- you want to code, the code if the truth, you don't need more
- what about a nice report on the mapping quality in your browser ?

# You should know
I've made this program in one day, it has been made with the StringSimilarity.java class found on the web. This code has been tested with Java 6 && 7, it's not optimized and not respectful towards some basic Java good practices, but it does the job. And I hope it will help !

# Why ?
I use to map JAXB annotated types one to anothers, all days, at work. We spend 90% of our time writing java mappers for our clients to integrate new web services based on XML. Since we use CXF and JAXB code generation from XSD, it's possible to automate the writing of 50% of our mappers, at least for fields that are similar by their name/type/attributes...
According to your data model, this little program writes 50% of the mapping code for you, sometimes more! Take a look at the report to state on the quality of the generated code :
![alt text](https://github.com/gillesofraisse/dontCodeTheMapper/blob/master/generator/htmlReport.png "Sample")

# Use
1. Add the generator and javax.validation (JSR 303) to the classpath
2. Add the 2 JAXB annotated types (A && B) and jaxb-api to the classpath
3. Ensure your 2 types have JAXB annotations XmlElement, XmlType and Size (optional). For size you can use the krasa-jaxb-tools generator plugin !
4. Call: 
```java
System.out.println(new Generator().mapAToB(A, B, true, true, true));
```
Get the result in console:
```java
//creates...
AccountPeople account1Dest = new AccountPeople();
cible.setAccount( account1Dest );
DataInfos profileInfos1Dest = new DataInfos();
account1Dest.setProfileInfos( profileInfos1Dest );...
//sets...
cible.setId( source.getId());
Account account1Source = source.getAccount();
if ( account1Source != null ){
account1Dest.setLogin( account1Source.getLogin());
account1Dest.setIsAdmin( formatString ( account1Source.getLogin(), 1, 1 ));
account1Dest.setPassword( account1Source.getPassword());
}
cible.setFirstName( formatString ( source.getFirstName(), 0, 20 ));
//unsets...
//profileInfos1Dest.setCreation( new XMLGregorianCalendar() );
//profileInfos1Dest.setModification( new XMLGregorianCalendar() );

//toString...
//toHTML... 
```

# Tips
* Once mapAToB() called, put the Java part of the result in your final mapping Class
* instanciate the "source" variable (type A)
* instanciate the "target" variable of type B
* get the formatString() method from the src/test/java/com/java/jaxb/cool package and add it in your mapper
* the generated code is quite ready to compile
* the formatMe() method is to be implemented, if necessary
* take a look at the generated comments
* run the generated HTML code in an HTML runner like http://codebeautify.org/htmlviewer/# and see the result. It represents a tree of the B type, with fields names, types and metadatas, and the mapping computed from the A type with a mapping quality percentage. The color is related to this percentage:
* Green means the mapping code is quite good
* Red means that the mapping code is to checked
* Black means no mapping has been done

# More tips
* Have a look at the L class, you can play with the INDEX_RESSEMBLANCE variable to get a totally different mapping code
* Always check the generated mapping code before committing :)


# Limits
* Doesn't generate the mapping code for collections (instead it writes a comment "//a.setList( new List());"), not yet !

