# Detectron trainings visualization
With this software you can visualize your Detectron training process. The software reads the accuracy, the loss, the loss_cls and the loss_bbox. Optionally you can create regressions for the different series in different orders. You can also import two training stats if you trained in two diferent steps.

When you train you network just save the standard out into a file and paste it into the software directory. For example use this command: 
```
python2 tools/train_net.py --cfg path/to/config/file.yaml OUTPUT_DIR path/to/output/directory | tee /home/ubuntu/output/trainingsoutput.txt
```

You can configure the chart with the settings at the top of the script. Select the lower and higher boundaries as well as the ticks. Choose which data you want to show and if you want to create a regression of the accuracy.


When you run the software you should get a chart like this:
![example chart](https://github.com/mattifrind/detectron-trainings-visualization/blob/master/output.png)

For more information visit my [website](http://matti.frind.de).

## References
- [JSON library](https://mvnrepository.com/artifact/org.json/json)
- [Thorwin math](http://www.thorwin.nl/)
