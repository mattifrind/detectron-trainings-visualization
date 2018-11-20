# Detectron trainings visualization
With this software you can visualize your Detectron training process. The software reads the accuracy, the loss, the loss_cls and the loss_bbox. 

To get started just unpack the vis-tool.zip and start the "train-vis.jar" in the zip-folder "vis-tool.zip".

When you train you network just save the standard out into a file and paste it into the software directory. For example use this command: 
```
python2 tools/train_net.py --cfg path/to/config/file.yaml OUTPUT_DIR path/to/output/directory | tee /home/ubuntu/output/trainingsoutput.txt
```

You can configure the chart with the form at the start of the tool. Select the higher boundaries as well as the ticks and choose which data you want to show. You can also create a linear regression of the accuracy.

When you run the software you should get a chart like this:
![example chart](https://github.com/mattifrind/detectron-trainings-visualization/blob/master/version2.png)

For more information visit my [website](http://matti.frind.de).

## References
- [JSON library](https://mvnrepository.com/artifact/org.json/json)
- [Thorwin math](http://www.thorwin.nl/)
