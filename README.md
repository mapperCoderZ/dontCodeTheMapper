# Don't code the mapper! 

This program writes the java mapping code for you. Java is verbose!
- you do not want anymore to write if (null) then else
- you want to generate the code that maps basic fields without coding
- you want to keep control on the code
- you dislike mappers based on configuration files
- you are a developer
- what about a nice report in your browser ?
# Why ?
I wrote this program in one day, because I use to map JaxB annotated types one to anothers, all days, at work. We spend 90% of our time writing java mappers for our clients, to integrate new web services based on XML. Since we use CXF and JaxB code generation from XSD, it's possible to automate the writing of 50% of our mappers,  basically for fields wich are similar, by their name/type/attributes...
According to your data model, this little program writes 50% of the mapping code for you, sometimes more! Take a look at the report to state on the quality of the generated mapping :
![alt text](https://github.com/gillesofraisse/dontCodeTheMapper/blob/master/generator/htmlReport.png "Sample")
# Use






