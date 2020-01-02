## ---------------------------------------------------------
## DISC USAGE (du -cksh *)
## Build context to Docker daemon  476.7kB
##
## Ubuntu Eoan image base -> 78M
## After APT update -> 99M (+21M)
## After installing Wget -> 106M (+7M)
## After installing OpenJDK 11 -> 309M (+203M)
## After installing Leiningen -> 323M (+14M)
## After copied the  project files -> 323M (+36K)
## After downloaded Leiningen project dependencies -> 356M (+33M)
## After created standalone artifact (.jar) -> 368M (+12M)
## After uninstalled packages and cleaned up system -> 317M (-51M)
## Size image committed -> 317M
## Size image builded -> 384M
## ---------------------------------------------------------

# Ubuntu eoan -> 19.10
FROM ubuntu:eoan

# Creating labels
LABEL version="1.0.2" description="Purchase microservices demo" "br.com.chinnonsantos.vendor"="Chinnon Santos - Software Engineer" "br.com.chinnonsantos.email"="contato@chinnonsantos.com.br" "br.com.chinnonsantos.repository"="https://github.com/chinnonsantos/purchase-service"

# Updating Ubuntu APT packages
RUN ["sh", "-c", "apt-get update && apt-get upgrade -y"]
# Installing Wget package
RUN ["sh", "-c", "apt-get install wget -y"]
# Installing OpenJDK 11
RUN ["sh", "-c", "apt-get install openjdk-11-jre-headless -y && java --version"]
# Installing Leiningen
WORKDIR /usr/local/bin
RUN ["sh", "-c", "wget -q https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein"]
RUN ["sh", "-c", "chmod +x lein"]
RUN ["sh", "-c", "./lein &> /dev/null"]
RUN ["sh", "-c", "lein -v"]

# Creating app directory and set Workdir
RUN ["sh", "-c", "mkdir /home/purchase-service"]
WORKDIR /home/purchase-service

# Copying project files to container
COPY ["/src", "./src"]
COPY ["project.clj", "."]

# Downloading Leiningen project dependencies
RUN ["sh", "-c", "lein deps &> /dev/null"]

# Create standalone artifact (.jar)
RUN ["sh", "-c", "lein ring uberjar"]

# Uninstalling packages and cleaning the system for reduce container size
RUN ["sh", "-c", "apt-get remove wget -y && apt-get autoremove -y && apt-get autoclean -y && apt-get clean -y"]
RUN ["sh", "-c", "cp target/purchase-1.0.2.jar ."]
RUN ["sh", "-c", "rm -rf /var/lib/apt/lists/ /tmp/ /var/tmp/ /root/.lein/ /root/.m2/ /root/.wget-hsts /usr/local/bin/lein /home/purchase-service/project.clj /home/purchase-service/src/ /home/purchase-service/target/ /home/purchase-service/test/"]

# Specifying network ports
EXPOSE 9002/tcp

# Starting service (run standalone artifact)
CMD ["purchase-1.0.2.jar"]
ENTRYPOINT ["java", "-jar"]