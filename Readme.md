# Projet ESIR MDI Docker

Hello très chers étudiants,

Le but de ce TP est de regarder comment nous pouvons utiliser docker pour faciliter le déploiement d'une application avec la mise en place d'un serveur web en reverse proxy etc...


<!--more-->

### Etape -1: Docker

Si vous utilisez une machine perso, installez docker (voir [ici](http://olivier.barais.fr/blog/posts/teaching/istic/m2/french/2018/09/10/Operation_portable_M2_ISTIC.html))



### Etape 0: Test de votre installation ###

```bash
docker run hello-world
```

Vous devriez avoir le message suivant. 


----

Hello from Docker.
This message shows that your installation appears to be working correctly.

To generate this message, Docker took the following steps:
 1. The Docker client contacted the Docker daemon.
 2. The Docker daemon pulled the "hello-world" image from the Docker Hub.
 3. The Docker daemon created a new container from that image which runs the
    executable that produces the output you are currently reading.
 4. The Docker daemon streamed that output to the Docker client, which sent it
    to your terminal.

To try something more ambitious, you can run an Ubuntu container with:
 $ docker run -it ubuntu bash

Share images, automate workflows, and more with a free Docker Hub account:
 https://hub.docker.com

For more examples and ideas, visit:
 https://docs.docker.com/userguide/

 
----


```bash
docker run -t -i ubuntu /bin/bash
```

Vous récupérez un shell qui est différent de votre propre distribution. 

Tapez la commande 

```bash
apt-get update
apt-get install net-tools
/sbin/ifconfig
```

Vous constatez que l'interface réseau n'est pas la même dans le container et dans la machine hote. 

Le container vient avec sa propre interface réseau. 



### Etape 1: Jouons avec docker: mise en place d'un load balancer et d'un reverse proxy avec docker et nginx

Pour le nginx en resolproxy nous allons partir de l'image [suivante](https://github.com/jwilder/nginx-proxy)

L'explication du fonctionnement est disponible [ici](http://jasonwilder.com/blog/2014/03/25/automated-nginx-reverse-proxy-for-docker/). 


Lancement de nginx en resolvproxy

```bash
docker run -d -p 8080:80 -v /var/run/docker.sock:/tmp/docker.sock -t jwilder/nginx-proxy 
```


Dans le suite nous allons utiliser terminator pour visualiser les effets du load-balancing (uniquement pour ceux qui sont sur leur propre portable). 

```bash
apt-get install terminator
```

Lancez Terminator en root.

```bash
sudo terminator
```


Si vous êtes sur votre propre portable, modifiez votre fichier /etc/hosts pour faire correspondre **m** vers localhost. Ce serait à faire sur votre gestionnaire de nom de domaine en temps normal.

Vous devez avoir une ligne qui ressemble à cela. 

```txt
127.0.0.1	localhost localhost.localdomain localhost4 localhost4.localdomain m
```


Pour ceux qui n'ont pas les droits root, exécutez les commandes suivantes

```bash
echo 'm localhost' >> ~/.hosts
export HOSTALIASES=~/.hosts
curl m:8080
```


Puis créer n fenètre dans votre navigateur terminator (clic droit puis split horizontal ou vertical). 
Dans ces terminales, lancez la commande suivante pour tester votre resolve proxy.

```bash
docker run -e VIRTUAL_HOST=m -t -i  nginx
```

Testez votre resolv proxy en lançant la commande suivante. 

```bash
curl m:8080
```


En tapant la commande suivante, vous pouvez regarder le fichier de configuration nginx qui sera généré à l'adresse suivante /etc/nginx/conf.d/default.conf. (N'oubliez pas de remplacer  865c1e67a00e par l'id de votre nginx en resolve proxy ($docker ps) pour récupérer la liste des containers en cours d'exécution.

```bash
docker exec -it 865c1e67a00e bash
```
barais@kevtop2:/media/barais/ed91608b-85b0-46e5-919c-ade3798e6dd6/home/barais/workspaces/tps/projetESIR$ ls

- [source](http://jasonwilder.com/blog/2014/03/25/automated-nginx-reverse-proxy-for-docker/)


Tuez tous les dockers nginx démarrer. 

```bash
docker ps #pour avoir la liste
docker kill "IDDOCKER" #pour tuer un docker. 
```

### Etape 2: Utilisation de docker compose
Utilisez docker compose pour déployer votre vos 4 services nginx et votre loadbalancer. 

[tutoriel](https://docs.docker.com/get-started/part3/)


### Etape 3: Dockeriser une application existante

Nous souhaitons partir d'une application Web de détection de visage. 

Src dans ce repository

https://github.com/barais/ESIRTPDockerSampleApp (Une documentation pour compiler et lancer cette application est disponible à la fin de ce README.

Construisez le fichier docker file permettant de créer l'image docker pour cette application. 

Vous aurez besoin de construire open cv depuis les src (principalement depuis la version 3.4)

https://github.com/opencv/opencv


Tester le lancement de cette image. 

Vous pourrez utiliser cette documentation pour la compilation d'opencv sur ubuntu.

https://advancedweb.hu/2016/03/01/opencv_ubuntu/

N'oubliez pas d'installer ant au sein de votre image docker ainsi que la jvm et maven. 


Nous souhaitons faire en sorte de fournir une image docker finale la plus petite possible. (Un paquet de carambar à la plus petite image fonctionnelle)

Fournissez donc deux fichiers docker file, un premier pour construire l'image qui permet de compiler opencv et compiler votre application. Un deuxième qui permet de construire l'image minimale pour votre application. 

### Etape 4: Dockeriser une application existante

Fournir un docker file qui permet de mettre en place une application avec 4 instance de votre serveur Web. 


### Etape 5: Dockeriser une application existante

En utilisant [https://labs.play-with-k8s.com/](https://labs.play-with-k8s.com/), déployer votre service à l'aide de kubernetes. 



# How to compile this application

Simple example of using OpenCV in a Web application build using jersey.

This application takes a picture using web browsers camera API (available  in modern browsers) 
and runs OpenCV face recognition algorithm (using [CascadeClassifier](http://docs.opencv.org/java/org/opencv/objdetect/CascadeClassifier.html) ) for it. If a face is detected a "troll face" is added  on top of it.

![Screenshot](/screenshot.png?raw=true "Screenshot")

This application was inspired by the ingenious ["Trollator" mobile Android application](https://play.google.com/store/apps/details?id=com.fredagapps.android.trollator).

1. OpenCV Installation for local Maven repository
---
OpenCV is a native library with Java bindings so you need to install this to your system.
 - *libopencv_java346.so* installed in you java.library.path (
 - *opencv-346.jar* availble for application

There are good instructions how to build OpenCV with Java bindings for your own platform here: http://docs.opencv.org/doc/tutorials/introduction/desktop_java/java_dev_intro.html

Once you have built the Java library you can install the resulting jar file to your local Maven repository using
     mvn install:install-file -Dfile=./bin/opencv-346.jar \
     -DgroupId=org.opencv  -DartifactId=opencv -Dversion=3.4.6 -Dpackaging=jar


2. Building this application
----
Once OpenCV jar library is available as a local Maven dependency, you can clone and build this application simply using Git and Maven:


     mvn install

And run the application using the embedded Jetty plugin in http://localhost:8888

     mvn package
     java -Djava.library.path=/home/barais/git/opencv/build/lib/ -jar target/fatjar-0.0.1-SNAPSHOT.jar
	# Do not forget to update the path to your opencv install in Main.java
	# You can change the image trollface ;)

# Fork this repo


If you fork this repo, to be up to date. 

```sh
git remote add upstream https://github.com/barais/projetWE
git fetch upstream
git checkout master
git merge upstream/master
```

