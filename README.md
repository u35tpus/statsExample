# statsExample
Test program for calculating online stats: min, max, mean




# Features
* BigDecimal support
* Thread Safe
*  100% branch test coverage: 
 ![alt text](https://raw.githubusercontent.com/u35tpus/statsExample/master/screenshots/coverage.png)

# Requirements
* Java 8
 
# Howto
## Compile and run tests
  mvn clean package
  
## Usage
### Starting
  java -jar target/tst-1.0-SNAPSHOT.jar
  
  
```
Enter number. Press CTRL-C or type 'exit' to exit. Enter S for stats
12.3E+7
Enter number. Press CTRL-C or type 'exit' to exit. Enter S for stats
12
Enter number. Press CTRL-C or type 'exit' to exit. Enter S for stats
3
Enter number. Press CTRL-C or type 'exit' to exit. Enter S for stats
s
min 3.0000000000 [3]
max 123000000.0000000000 [1.23E+8]
mean 41000005.0000000000 [41000005]
Enter number. Press CTRL-C or type 'exit' to exit. Enter S for statsexit
18.2E+120
Enter number. Press CTRL-C or type 'exit' to exit. Enter S for stats
s
min 3.0000000000 [3]
max 18200000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000.0000000000 [1.82E+121]
mean 4550000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000030750004.0000000000 [4550000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000030750004]
Enter number. Press CTRL-C or type 'exit' to exit. Enter S for stats
```  

### Interaction
* Enter numbers in the following format: 
  * `optional sign, '+' ( '\u002B') or '-' ('\u002D'), followed by a sequence of zero or more decimal digits ("the integer"), optionally followed by a fraction, optionally followed by an exponent.`
* To get list of statistics, type `s` and press Enter. Each statistics is rendered in two formats:
  * optional sign, '+' ( '\u002B') or '-' ('\u002D'), followed by a sequence of zero or more decimal digits ("the integer"), optionally followed by a fraction, optionally followed by an exponent.
  * string representation of BigDecimal format in square brackets, e.g. [1.23E+8]
* To exit, type `exit` and press Enter. Or just press `CTRL+C`
