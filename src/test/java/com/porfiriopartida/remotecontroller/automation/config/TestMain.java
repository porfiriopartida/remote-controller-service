package com.porfiriopartida.remotecontroller.automation.config;

import org.junit.Assert;
import org.junit.Test;

public class TestMain {
    @Test
    public void testAdd(){
        int n = 5;
        Autolavado autolavado = new Autolavado(n);
        int data[] = autolavado.getData();

        Assert.assertNotNull(data);
        Assert.assertEquals(5, data.length);

        boolean res;
        res = autolavado.add(-5);

        Assert.assertTrue(res);
        Assert.assertEquals(5, autolavado.getData().length);
        Assert.assertEquals(-5, autolavado.getData()[0]);
        Assert.assertEquals(0, autolavado.getData()[1]);
        Assert.assertEquals(0, autolavado.getData()[2]);
        Assert.assertEquals(0, autolavado.getData()[3]);
        Assert.assertEquals(0, autolavado.getData()[4]);

        res = autolavado.add(-8);
        Assert.assertTrue(res);
        Assert.assertEquals(5, autolavado.getData().length);
        Assert.assertEquals(-5, autolavado.getData()[0]);
        Assert.assertEquals(-8, autolavado.getData()[1]);
        Assert.assertEquals(0, autolavado.getData()[2]);
        Assert.assertEquals(0, autolavado.getData()[3]);
        Assert.assertEquals(0, autolavado.getData()[4]);

        res = autolavado.add(-3);
        Assert.assertTrue(res);
        res = autolavado.add(-9);
        Assert.assertTrue(res);
        res = autolavado.add(-6);
        Assert.assertTrue(res);
        Assert.assertEquals(5, autolavado.getData().length);
        Assert.assertEquals(-5, autolavado.getData()[0]);
        Assert.assertEquals(-8, autolavado.getData()[1]);
        Assert.assertEquals(-3, autolavado.getData()[2]);
        Assert.assertEquals(-9, autolavado.getData()[3]);
        Assert.assertEquals(-6, autolavado.getData()[4]);


        res = autolavado.add(0);
        Assert.assertFalse("Add 0 regreso true.", res);
        res = autolavado.add(0);
        Assert.assertFalse("Add 0 regreso true.", res);
        res = autolavado.add(0);
        Assert.assertFalse("Add 0 regreso true.", res);

        Assert.assertEquals(5, autolavado.getData().length);
        Assert.assertEquals(-5, autolavado.getData()[0]);
        Assert.assertEquals(-8, autolavado.getData()[1]);
        Assert.assertEquals(-3, autolavado.getData()[2]);
        Assert.assertEquals(-9, autolavado.getData()[3]);
        Assert.assertEquals(-6, autolavado.getData()[4]);
    }

    @Test
    public void testExtracts(){
        Autolavado autolavado = getGenericAutolavado();
        int extracted;
        extracted = autolavado.extract();

        Assert.assertEquals(-5, extracted);
        extracted = autolavado.extract();
        Assert.assertEquals(-8, extracted);
        extracted = autolavado.extract();
        Assert.assertEquals(-3, extracted);

        Assert.assertEquals(-9, autolavado.getData()[0]);
        Assert.assertEquals(-6, autolavado.getData()[1]);
        Assert.assertEquals(0, autolavado.getData()[2]);
        Assert.assertEquals(0, autolavado.getData()[3]);
        Assert.assertEquals(0, autolavado.getData()[4]);

        extracted = autolavado.extract();
        Assert.assertEquals(-9, extracted);
        extracted = autolavado.extract();
        Assert.assertEquals(-6, extracted);

        extracted = autolavado.extract();
        Assert.assertEquals(Autolavado.EMPTY_VALUE, extracted);
    }

    @Test
    public void testLavar(){
        Autolavado autolavado = getGenericAutolavado();
        int val = autolavado.extract();
        autolavado.lavar(val);
        Assert.assertEquals(val, autolavado.getLavado());
    }
    @Test
    public void testAspirar(){
        Autolavado autolavado = getGenericAutolavado();
        int val = autolavado.extract();
        autolavado.lavar(val);
        autolavado.aspirar();

        Assert.assertEquals(val, autolavado.getLavado());
    }

    private Autolavado getGenericAutolavado(){
        Autolavado autolavado = new Autolavado(5);
        autolavado.add(-5);
        autolavado.add(-8);
        autolavado.add(-3);
        autolavado.add(-9);
        autolavado.add(-6);
        return autolavado;
    }
    private static class Autolavado {
        public static final int EMPTY_VALUE = -99999;
        private int[] data;
        private int lavado = -1;
        private int pointer = 0;
        private int n;

        public Autolavado(int n) {
            data = new int[n];
            this.n = n;
        }

        public int[] getData() {
            return data;
        }
        public int extract(){
            int actual = EMPTY_VALUE;

            if(this.pointer > 0){
                actual = this.data[0];
                for (int i = 0; i < this.n - 1; i++) {
                    this.data[i] = this.data[i + 1];
                }
                this.pointer--;
                this.data[this.pointer] = 0;
            }

            return actual;
        }

        public boolean add(int i) {
            if(pointer < n){
                this.data[pointer] = i;
                pointer++;
                return true;
            }
            return false;
        }

        public void lavar(int val) {
            lavado = val;
        }

        public int getLavado() {
            return lavado;
        }

        public void aspirar() {
            lavado = -1;
        }
    }
}