import { Component, OnInit, Input, AfterViewInit, Output, EventEmitter } from '@angular/core';
import Tagify from '@yaireo/tagify';
import { TagStar } from '../tag-star';
import { Placeholder } from '@angular/compiler/src/i18n/i18n_ast';
import { mapToMapExpression } from '@angular/compiler/src/render3/util';

@Component({
  selector: 'app-tagify-stars',
  templateUrl: './tagify-stars.component.html',
  styleUrls: ['./tagify-stars.component.css']
})
export class TagifyStarsComponent implements AfterViewInit {

  @Input() whitelist = [];

  @Input() blacklist;

  @Input() originalValues;

  @Input() placeholder;

  @Input() colorON;

  @Input() colorOFF;

  @Output() addTagEvent = new EventEmitter<TagStar>();

  @Output() removeTagEvent = new EventEmitter<string>();

  tagify: Tagify;

  input: any;

  // Array of eventHandler bound the the tagigy-stars component.
  // The goal of these eventHandler is to catch & save the selected star.
  boundOnClick = new Array(5);

  // The selected star
  public star: number;


  ngAfterViewInit() {

    this.input = document.getElementById('tagify-stars');

    this.tagify = new Tagify(this.input, {
      enforceWhitelist: true,
      placeholder: this.placeholder,
      templates: {
        wrapper(input, settings) {
          return `<tags style="height:99%;width:99%;margin:1px;"
                class="tagify ${settings.mode ? 'tagify--mix' : ''} ${input.className}" ${settings.readonly ? 'readonly' : ''}>
                <span contenteditable data-placeholder="${settings.placeholder}" class="tagify__input"></span></tags>`;
        },
        tag(v, tagData) {
          return `<tag title='${v}' scontenteditable='false' spellcheck="false" class='tagify__tag
              ${tagData.class ? tagData.class : ''}' ${this.getAttributes(tagData)}>
                <x title='' class='tagify__tag__removeBtn'></x><div style="background-color:lightGrey" ><span class='tagify__tag-text'>
                  ${v}
                  <i class="fas fa-star" id='tag-star-${v}-0'></i>
                  <i class="fas fa-star" id='tag-star-${v}-1'></i>
                  <i class="fas fa-star" id='tag-star-${v}-2'></i>
                  <i class="fas fa-star" id='tag-star-${v}-3'></i>
                  <i class="fas fa-star" id='tag-star-${v}-4'></i>
                  </span>
                </div>
                </tag>`;
        }
      }
    }
    );

    this.whitelist.forEach(element => this.tagify.settings.whitelist.push(element));
    this.blacklist.forEach(element => this.tagify.settings.blacklist.push(element));
    this.tagify.settings.readonly = true;
    this.tagify.settings.placeholder = 'Nope';

    this.boundOnClick[0] = this.onClick_0.bind(this);
    this.boundOnClick[1] = this.onClick_1.bind(this);
    this.boundOnClick[2] = this.onClick_2.bind(this);
    this.boundOnClick[3] = this.onClick_3.bind(this);
    this.boundOnClick[4] = this.onClick_4.bind(this);

    this.tagify.addTags(this.originalValues.map(tagStar => tagStar.tag));
    this.originalValues.forEach(tagStar => {
      for (let i = 0; i <= tagStar.star; i++) {
        this.setColor(tagStar.tag, i, this.colorON);
      }
      for (let i = tagStar.star + 1; i < 5; i++) {
        this.setColor(tagStar.tag, i, this.colorOFF);
      }
    });

    // Chainable event listeners
    this.tagify.on('add', this.onAddTag.bind(this))
      .on('remove', this.onRemoveTag.bind(this))
      .on('click', this.onTagClick.bind(this));

    this.updateStars();

  }

  /**
   * Generate the key for a specific tag.
   * @param tag the searched tag
   * @param ind the associated level for the tag
   */
  private idStar(tag: string, ind: number) {
    return 'tag-star-' + tag + '-' + ind;
  }

  /**
   * Register an event handler for each star in the component.
   */
  updateStars() {
    this.tagify.value.forEach(tag => {
      for (let i = 0; i < 5; i++) {
        const id = this.idStar(tag.value, i);
        document.getElementById(id).onclick = this.boundOnClick[i];
      }
    });
  }

  /**
   * A new tag has been added in the list
   * @param e the associated tag
   */
  onAddTag(e: CustomEvent) {
    for (let i = 0; i < 5; i++) {
      const id = this.idStar(e.detail.data.value, i);
      document.getElementById(id).onclick = this.boundOnClick[i];
    }
    this.setColor(e.detail.data.value, 0, this.colorON);
    this.addTagEvent.emit(new TagStar(e.detail.data.value, 0));
  }

  /**
   * An existing tag has been removed from the list
   * @param e the associated tag
   */
  onRemoveTag(e: CustomEvent) {
    for (let i = 0; i < 5; i++) {
      const id = this.idStar(e.detail.data.value, i);
      document.getElementById(id).onclick = null;
    }
    this.removeTagEvent.emit(e.detail.data.value);
  }


  // invalid tag added callback
  onTagClick(e: CustomEvent) {
    const tag = e.detail.data.value;
    for (let i = 0; i <= this.star; i++) {
      this.setColor(tag, i, this.colorON);
    }
    for (let i = this.star + 1; i < 5; i++) {
      this.setColor(tag, i, this.colorOFF);
    }
    this.addTagEvent.emit(new TagStar(tag, this.star));
    this.star = 0;
  }

  /**
   * Set the color for a specific star of a tag
   * @param tag the tag
   * @param star the rank star inside the tag
   * @param color  the color to be set
   */
  setColor(tag: string, star: number, color: string) {
    document.getElementById(this.idStar(tag, star)).style.color = color;
  }

  /**
   * Save the number of the rank 'star'.
   */
  public onClick_0() {
    this.star = 0;
  }

  /**
   * Save the number of the rank 'star'.
   */
  public onClick_1() {
    this.star = 1;
  }

  /**
   * Save the number of the rank 'star'.
   */
  public onClick_2() {
    this.star = 2;
  }

  /**
   * Save the number of the rank 'star'.
   */
  public onClick_3() {
    this.star = 3;
  }

  /**
   * Save the number of the rank 'star'.
   */
  public onClick_4() {
    this.star = 4;
  }

}
