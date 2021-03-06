package com.company;

public class Trainer
{
    private NeuralNetwork nn;
    private double[][] question;
    private double[][] answer;
    private double errBound = 0.25;

    public Trainer(int inputs, int outputs, int layers, int[] nodes, double[][] question, double[][] answer)
    {
        nn = new NeuralNetwork(inputs, outputs, layers, nodes);
        nn.randomize(-10, 10);
        this.question = question;
        this.answer = answer;
    }

    void setSeed(int seed)
    {
        nn.randomize(-10, 10, (long)seed);
    }

    public void teachAll(int rep1, int rep2)
    {
        for(int j = 0; j < rep1; j++)
            for (int i = 0; i < question.length; i++)
                teach(rep2, question[i], answer[i]);
    }

    void teach(int repetitions, double[] question, double[] answer)
    {
        for(int i = 0; i < repetitions; i++)
        {
            nn.run(question);
            double error = nn.layer[nn.layer.length - 1].input[0] - answer[0];
            System.out.println(nn.layer[nn.layer.length - 1].input[0]);
            nn.computeErrorAndLearn(nn.layer[nn.layer.length - 1].input[0] - answer[0], i);
        }
    }

    void teachAllTillLearned(int rep)
    {
        int[][] tries = new int[question.length][rep];
        for(int j = 0; j < rep; j++)
            for (int i = 0; i < question.length; i++)
                tries[i][j] = teachTillLearned(question[i], answer[i], 0, false);

        for(int i = 0; i < tries[0].length; i++)
        {
            for (int j = 0; j < tries.length; j++)
                System.out.print(tries[j][i] + "  ");
            System.out.println();
        }
    }

    int teachTillLearned(double[] question, double[] answer, long start, boolean terminating)
    {
        for(int i = 0; true; i++)
        {
            if (System.currentTimeMillis() - start > 15000 && terminating)
                return -1;
            nn.run(question);
            double err = Math.abs(answer[0] - nn.layer[nn.layer.length - 1].input[0]);
            System.out.println(err);
            if (err < errBound && err > -errBound)
                return i;
            else
                nn.computeErrorAndLearn(nn.layer[nn.layer.length - 1].input[0] - answer[0], i);

        }
    }

    boolean test(int problems)
    {
        double[][] input = new double[121][2];
        double[][] output = new double[121][1];
        for(int i = 0; i < problems; i++)
        {
            input[i][0] = 1 + (i % 10);
            input[i][1] = 1 + (i / 10);
            output[i][0] = (1 + (i % 10)) * (1 + (i / 10));
        }

        for(int i = 0; i < problems; i++)
        {
            nn.run(input[i]);
            double err = Math.abs(nn.layer[nn.layer.length - 1].input[0] - output[i][0]);
            if (err > errBound)
                return false;
        }
        return true;
    }
}