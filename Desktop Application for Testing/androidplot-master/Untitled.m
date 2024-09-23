clc
close all
clear
c=47*(10^-12);
r1=10e3;
r2 = 470;
w = 0:3;
num = 1;
denum = [(c^2)*r1*r2 ((2*r1)+r2)*c 1];
h= tf(num,denum);
%bode(h)
 freqs(num,denum)
