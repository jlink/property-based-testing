Calculate how many years we are still allowed to emit CO2 into the environment
and still keep the probability of global temperature rise of under 2 degrees
at 66 percent?

### Estimate of 2018 
- Rest budget 420 Gt (giga tons) CO2
- Current annual output 41 Gt

### Function to implement

```
int remainingYears(int budget, int startingAnnualEmission, int annualChange)
```

### Specification

- All input is in Mt (mega tons). 1000 Mt == 1 Gt
- `budget` and `startingAnnualEmission` must be >= 0 otherwise signal illegal input
- ˋannualChangeˋ is applied every year on the previous year's annual starting in the 2nd year
- The year in which the budget is eventually used up still counts as one year
- If the budget starts with 0 the remaining years are also 0
