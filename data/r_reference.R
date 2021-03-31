df = read.csv("data\\ship_data.csv")

model = lm(perseverance_score~starfleet_gpa, data=df)
summary(model)
anova(model)

df2 = read.csv("data\\reale_data.csv")

model2 = lm(Sale~list, data=df2)
summary(model2)
anova(model2)
