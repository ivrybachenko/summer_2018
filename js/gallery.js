// Копирует изображение и информацию из from в to
function copy(from, to){
    var $sdescr;
    var $ldescr;
    if (from.find('.sdescr').prop('tagName')=='INPUT'){
        $sdescr = from.find('.sdescr').val();
    }
    else {
        $sdescr = from.find('.sdescr').html()
    }
    if (from.find('.ldescr').prop('tagName')=='INPUT'){
        $ldescr = from.find('.ldescr').val();
    }
    else {
        $ldescr = from.find('.ldescr').html()
    }

    to.find('img').attr('src', from.find('img').attr('src'));

    if (to.find('.sdescr').prop('tagName')=='INPUT'){
        to.find('.sdescr').val($sdescr);
    }
    else{
        to.find('.sdescr').html($sdescr);
    }
    if (to.find('.ldescr').prop('tagName')=='INPUT'){
        to.find('.ldescr').val($ldescr);
    }
    else{
        to.find('.ldescr').html($ldescr);
    }
};
// Копирует информацию из from в to с заменой картинки на полную версию
function copyF(from, to) {
    var $sdescr;
    var $ldescr;
    if (from.find('.sdescr').prop('tagName')=='INPUT'){
        $sdescr = from.find('.sdescr').val();
    }
    else {
        $sdescr = from.find('.sdescr').html()
    }
    if (from.find('.ldescr').prop('tagName')=='INPUT'){
        $ldescr = from.find('.ldescr').val();
    }
    else {
        $ldescr = from.find('.ldescr').html()
    }

    to.find('img').attr('src', from.find('img').attr('src').replace('tn','full'));

    if (to.find('.sdescr').prop('tagName')=='INPUT'){
        to.find('.sdescr').val($sdescr);
    }
    else{
        to.find('.sdescr').html($sdescr);
    }
    if (to.find('.ldescr').prop('tagName')=='INPUT'){
        to.find('.ldescr').val($ldescr);
    }
    else{
        to.find('.ldescr').html($ldescr);
    }
}