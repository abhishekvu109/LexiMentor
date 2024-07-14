Problem: What to do if your services can't resolve the DNS withing the kubernetes?
Solution: In my case the service was reachable via curl utility when accessed using the service IP. I checked the core-dns service that was running all good. However, the problem was that the external dns was not recognizing the request.

steps to solve it:
1. check if the NetworkManager exist or not
    `dpkg -l | grep network-manager`
2. if not then install
    `sudo apt update`
    `sudo apt install network-manager`
3. then, restart
    `sudo systemctl restart NetworkManager`
4. create file
    `sudo nano /etc/NetworkManager/conf.d/dns.conf`
5. Add the following
    [main]
    dns=none

    [global-dns]
    <!-- This is the kubernetes core dns ip -->
    servers=10.152.183.10
6. restart the network manager
    `sudo systemctl restart NetworkManager`


