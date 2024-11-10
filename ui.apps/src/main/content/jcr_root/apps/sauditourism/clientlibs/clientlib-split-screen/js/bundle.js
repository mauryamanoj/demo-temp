$(document).ready(function()
{
    if(window.innerWidth > 568){
        /* hovering over left column*/
        $('#a').hover(function()
        {
            $('.leftside').addClass('leftsidenew').removeClass('leftside')
            $('.rightside').addClass('rightnewfromleft').removeClass('rightside')

            $('#rightheader').addClass('fade')
            $('#righttext').addClass('fade')
            $('#rightbtn').addClass('fade')

        }, function() {
            $('.rightnewfromleft').addClass('rightside').removeClass('rightnewfromleft')
            $('.leftsidenew').addClass('leftside').removeClass('leftsidenew')
            $('#rightheader').removeClass('fade')
            $('#righttext').removeClass('fade')
            $('#rightbtn').removeClass('fade')
        })

        /* hovering over right column */
        $('#b').hover(function()
        {
            $('.rightside').addClass('rightsidenew').removeClass('rightside')
            $('.leftside').addClass('leftnewfromright').removeClass('leftside')

            $('#leftheader').addClass('fade')
            $('#lefttext').addClass('fade')
            $('#leftbtn').addClass('fade')

        }, function() {
            $('.rightsidenew').addClass('rightside').removeClass('rightsidenew')
            $('.leftnewfromright').addClass('leftside').removeClass('leftnewfromright')

            $('#leftheader').removeClass('fade')
            $('#lefttext').removeClass('fade')
            $('#leftbtn').removeClass('fade')
        })
    }
});