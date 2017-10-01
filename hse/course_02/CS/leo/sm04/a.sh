while true; do
    rm t
    touch t
    dd if=/dev/urandom of=t bs=$1 count=1 &>/dev/null
    ./3 - < t > a
    ./A t > b
    cat t
    diff a b || break
done
